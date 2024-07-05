package syu.autobiography.spring.audioupload.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import syu.autobiography.spring.entity.Posts;
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

    public void saveDraft(String transcript, int questionNumber, Users user) {
        Posts post = new Posts();
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setDraftText(transcript);
        post.setQuestionNumber(questionNumber);

        audioRepository.save(post);
    }

    public String generateFinalDraft(Users user) {
        List<Posts> latestPosts = audioRepository.getLatestPostsForAllQuestions(user.getUserNo());
        StringBuilder content = new StringBuilder();

        for (Posts post : latestPosts) {
            content.append("Question ").append(post.getQuestionNumber()).append(": ");
            content.append(post.getDraftText()).append(" ");
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
