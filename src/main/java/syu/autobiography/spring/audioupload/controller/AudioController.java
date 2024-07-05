package syu.autobiography.spring.audioupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import syu.autobiography.spring.audioupload.service.AudioService;
import syu.autobiography.spring.entity.Posts;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.audioupload.repository.AudioRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class AudioController {

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    @Autowired
    private AudioService audioService;

    @Autowired
    private AudioRepository audioRepository;

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam(value = "questionNumber", required = false, defaultValue = "1") int questionNumber, HttpSession session) {
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not logged in"));
        }

        try {
            byte[] bytes = file.getBytes();
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.postForEntity(flaskApiUrl + "/upload", requestEntity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                String transcript = (String) responseBody.get("transcript");
                audioService.saveDraft(transcript, questionNumber, user);

                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to get response from the server"));
            }

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTranscript(@RequestBody Map<String, String> payload, HttpSession session) {
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not logged in"));
        }

        String updatedTranscript = payload.get("transcript");
        int questionNumber = Integer.parseInt(payload.get("questionNumber"));

        audioService.saveDraft(updatedTranscript, questionNumber, user);

        return ResponseEntity.ok(Map.of("message", "Transcript updated successfully"));
    }

    @PostMapping("/generate-final-draft")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateFinalDraft(HttpSession session) {
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not logged in"));
        }

        try {
            List<Posts> allPosts = audioRepository.findAllByOrderByQuestionNumberAsc();
            if (allPosts.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "No posts found"));
            }

            StringBuilder contentBuilder = new StringBuilder();
            for (Posts post : allPosts) {
                contentBuilder.append("Question ").append(post.getQuestionNumber()).append(":\n");
                contentBuilder.append(post.getDraftText()).append("\n\n");
            }

            String compiledContent = contentBuilder.toString();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = Map.of("content", compiledContent);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(flaskApiUrl + "/generate-final-draft", request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to generate final draft"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate final draft: " + e.getMessage()));
        }
    }

    @GetMapping("/final-draft")
    public String showFinalDraft(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");

        if (user == null) {
            throw new IllegalStateException("User is not logged in");
        }

        String finalDraft = audioService.generateFinalDraft(user);
        model.addAttribute("finalDraft", finalDraft);
        return "fileupload/finaldraft";
    }
}