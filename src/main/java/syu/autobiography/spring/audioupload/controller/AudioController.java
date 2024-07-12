package syu.autobiography.spring.audioupload.controller;

import jakarta.validation.ConstraintViolationException;
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
import java.time.LocalDateTime;
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

                Posts newPost = new Posts();
                newPost.setUser(user);
                newPost.setCreatedAt(LocalDateTime.now());
                newPost.setDraftText(transcript);
                newPost.setQuestionNumber(questionNumber);
                newPost.setIsPublic("N");
                audioService.savePost(newPost);

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

        Posts post = audioService.findPostByUserAndQuestionNumber(user, questionNumber);
        if (post == null) {
            post = new Posts();
            post.setUser(user);
            post.setCreatedAt(LocalDateTime.now());
            post.setQuestionNumber(questionNumber);
            post.setIsPublic("N");
        }
        post.setDraftText(updatedTranscript);
        post.setUpdatedAt(LocalDateTime.now());
        audioService.savePost(post);

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
            Map<String, String> result = audioService.generateFinalDraft(user);
            if (!result.containsKey("error")) {
                // 생성된 초안과 제목을 세션에 저장
                session.setAttribute("guideline", result.get("guideline"));
                session.setAttribute("title", result.get("title"));
                return ResponseEntity.ok(Map.of("success", "Final draft generated successfully"));
            } else {
                return ResponseEntity.status(500).body(result);
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid title length. Title must be between 5 and 100 characters."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate final draft: " + e.getMessage()));
        }
    }

    @GetMapping("/final-draft")
    public String showFinalDraft(Model model, HttpSession session) {
        String guideline = (String) session.getAttribute("guideline");
        String title = (String) session.getAttribute("title");

        if (guideline == null || title == null) {
            // 초안이 생성되지 않았다면 에러 페이지나 다른 페이지로 리디렉션
            return "redirect:/error";
        }

        model.addAttribute("guideline", guideline);
        model.addAttribute("title", title);

        // 세션에서 초안과 제목 제거
        session.removeAttribute("guideline");
        session.removeAttribute("title");

        return "fileupload/finaldraft";
    }
}