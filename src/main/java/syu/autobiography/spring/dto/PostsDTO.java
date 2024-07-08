package syu.autobiography.spring.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PostsDTO {
    private int postsId;
    private int userNo;
    private int questionNumber;
    private String draftText;
    private String gptText;
    private String finalText;
    private String title;
    private String isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userName;
    private int userAge;
    private int likesCount;
    private boolean liked;


    public PostsDTO(int postsId, int userNo, int questionNumber, String draftText, String gptText, String finalText, String title, String isPublic, LocalDateTime createdAt, LocalDateTime updatedAt, String userName, int userAge, int likesCount, boolean liked) {
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
        this.userName = userName;
        this.userAge = userAge;
        this.likesCount = likesCount;
        this.liked = liked;
    }
}
