package syu.autobiography.spring.audioupload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import syu.autobiography.spring.audioupload.service.AudioService;

import java.io.IOException;
import java.util.Map;

@Controller
public class AudioController {

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    @Autowired
    private AudioService audioService;

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
                String guideline = (String) responseBody.get("guideline");
                audioService.saveDraft(transcript, guideline, chapter); // 데이터베이스에 저장

                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to get response from the server"));
            }

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    @PostMapping("/regenerate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> regenerateGuideline(@RequestBody Map<String, String> payload) {
        String updatedTranscript = payload.get("transcript");
        int chapter = Integer.parseInt(payload.get("chapter"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(Map.of("transcript", updatedTranscript), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(flaskApiUrl + "/regenerate", requestEntity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            String newTranscript = (String) responseBody.get("transcript");
            String newGuideline = (String) responseBody.get("guideline");

            audioService.saveDraft(newTranscript, newGuideline, chapter);

            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get response from the server"));
        }
    }
}
