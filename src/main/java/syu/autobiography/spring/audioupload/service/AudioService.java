package syu.autobiography.spring.audioupload.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import syu.autobiography.spring.entity.Drafts;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.audioupload.repository.AudioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AudioService {

    @Autowired
    private AudioRepository audioRepository;

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    public void saveDraft(String transcript, int chapter) {
        Users user = new Users();
        user.setUserNo(1);

        Drafts draft = new Drafts();
        draft.setUsers(user);
        draft.setRequestTime(LocalDateTime.now());
        draft.setDraftContent(transcript);
        draft.setChapterNumber(chapter);

        audioRepository.save(draft);
    }

    public String generateFinalDraft() {
        List<Drafts> latestDrafts = audioRepository.getLatestDraftsForAllChapters();
        StringBuilder content = new StringBuilder();

        for (Drafts draft : latestDrafts) {
            content.append("Chapter ").append(draft.getChapterNumber()).append(": ");
            content.append(draft.getDraftContent()).append(" ");
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = Map.of("content", content.toString());
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(flaskApiUrl + "/generate-final-draft", request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return (String) response.getBody().get("finalDraft");
        } else {
            return "Failed to generate final draft";
        }
    }
}