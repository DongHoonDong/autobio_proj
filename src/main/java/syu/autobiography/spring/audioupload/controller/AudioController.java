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
import syu.autobiography.spring.audioupload.service.AudioService;
import syu.autobiography.spring.entity.Drafts;
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
    public ResponseEntity<Map<String, Object>> handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("chapter") int chapter) {
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
                audioService.saveDraft(transcript, chapter);

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
    public ResponseEntity<Map<String, Object>> updateTranscript(@RequestBody Map<String, String> payload) {
        String updatedTranscript = payload.get("transcript");
        int chapter = Integer.parseInt(payload.get("chapter"));

        audioService.saveDraft(updatedTranscript, chapter);

        return ResponseEntity.ok(Map.of("message", "Transcript updated successfully"));
    }

    @PostMapping("/generate-final-draft")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateFinalDraft() {
        try {
            List<Drafts> allDrafts = audioRepository.findAllByOrderByChapterNumberAsc();
            if (allDrafts.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "No drafts found"));
            }

            StringBuilder contentBuilder = new StringBuilder();
            for (Drafts draft : allDrafts) {
                contentBuilder.append("Chapter ").append(draft.getChapterNumber()).append(":\n");
                contentBuilder.append(draft.getDraftContent()).append("\n\n");
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
    public String showFinalDraft(Model model) {
        String finalDraft = audioService.generateFinalDraft();
        model.addAttribute("finalDraft", finalDraft);
        return "fileupload/finaldraft";
    }
}