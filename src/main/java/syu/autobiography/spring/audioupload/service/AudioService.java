package syu.autobiography.spring.audioupload.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import syu.autobiography.spring.entity.Posts;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.audioupload.repository.AudioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AudioService {

    @Autowired
    private AudioRepository audioRepository;

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    public void savePost(Posts post) {
        audioRepository.save(post);
    }

    public Posts findPostByUserAndQuestionNumber(Users user, int questionNumber) {
        return audioRepository.findByUserAndQuestionNumber(user, questionNumber);
    }

    public Posts getDraftByUser(Users user) {
        return audioRepository.findByUserAndQuestionNumber(user, 0);
    }

    public void saveFinalDraft(String finalText, String title, Users user) {
        Posts finalDraft = audioRepository.findByUserAndQuestionNumber(user, 0);
        if (finalDraft == null) {
            finalDraft = new Posts();
            finalDraft.setUser(user);
            finalDraft.setQuestionNumber(0);
            finalDraft.setCreatedAt(LocalDateTime.now());
        }
        finalDraft.setFinalText(finalText);
        finalDraft.setTitle(limitTitleLength(title));
        finalDraft.setUpdatedAt(LocalDateTime.now());
        audioRepository.save(finalDraft);
    }

    private String limitTitleLength(String title) {
        final int MAX_TITLE_LENGTH = 100;
        if (title == null) {
            return "Untitled";
        }
        return title.length() > MAX_TITLE_LENGTH ? title.substring(0, MAX_TITLE_LENGTH) : title;
    }

    public Map<String, String> generateFinalDraft(Users user) {
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
            String guideline = (String) response.getBody().get("guideline");
            String title = (String) response.getBody().get("title");

            if (guideline == null) guideline = "Failed to generate guideline";
            if (title == null) title = "Untitled";

            saveFinalDraft(guideline, title, user);
            return Map.of("guideline", guideline, "title", title);
        } else {
            return Map.of("error", "Failed to generate final draft");
        }
    }

    public Posts getFinalDraftByUser(Users user) {
        return audioRepository.findByUserAndQuestionNumber(user, 0);
    }

    public void updateFinalDraft(String finalText, Users user) {
        Posts finalDraft = audioRepository.findByUserAndQuestionNumber(user, 0);
        if (finalDraft == null) {
            finalDraft = new Posts();
            finalDraft.setUser(user);
            finalDraft.setQuestionNumber(0);
            finalDraft.setCreatedAt(LocalDateTime.now());
        }
        finalDraft.setFinalText(finalText);
        finalDraft.setUpdatedAt(LocalDateTime.now());
        audioRepository.save(finalDraft);
    }
}