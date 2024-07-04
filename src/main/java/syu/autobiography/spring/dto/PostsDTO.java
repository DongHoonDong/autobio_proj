package syu.autobiography.spring.dto;

import lombok.*;
import syu.autobiography.spring.entity.Users;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @ToString
public class PostsDTO {
    private int postsId;
    private int userNo;
    private int questionNumber;
    private String draftText;
    private String gptText;
    private String finalText;
    private String title;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostsDTO(int postsId, int userNo, int questionNumber, String draftText, String gptText, String finalText, String title, boolean isPublic, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postsId = postsId;
        this.userNo = userNo;
        this.questionNumber = questionNumber;
        this.draftText = draftText;
        this.gptText = gptText;
        this.finalText = finalText;
        this.title = title;
        this.isPublic = isPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}