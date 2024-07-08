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

//나중에 혹시 모를 추가 기능 구현을 위한 댓글 DTO 입니다.

public class CommentDTO {
    private int commentId;
    private int chapterNumber;
    private int userNo;
    private String commentText;
    private LocalDateTime commentTime;

    public CommentDTO(int commentId, int chapterNumber, int userNo, String commentText, LocalDateTime commentTime) {
        this.commentId = commentId;
        this.chapterNumber = chapterNumber;
        this.userNo = userNo;
        this.commentText = commentText;
        this.commentTime = commentTime;
    }
}
