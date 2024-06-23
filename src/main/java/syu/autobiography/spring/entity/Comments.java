package syu.autobiography.spring.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "comments")

//나중에 혹시 모를 추가 기능 구현을 위한 댓글 엔티티 입니다.

public class Comments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

    @Column(name = "chapter_number", nullable = false)
    private int chapterNumber;

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private Users users;

    @Column(name = "comment_text", nullable = false, columnDefinition = "TEXT")
    private String commentText;

    @Column(name = "comment_time", nullable = false)
    private LocalDateTime commentTime;

    public Comments(int commentId, int chapterNumber, Users users, String commentText, LocalDateTime commentTime) {
        this.commentId = commentId;
        this.chapterNumber = chapterNumber;
        this.users = users;
        this.commentText = commentText;
        this.commentTime = commentTime;
    }
}
