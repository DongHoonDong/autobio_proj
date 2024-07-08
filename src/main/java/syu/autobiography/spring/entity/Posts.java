package syu.autobiography.spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "posts_id")
    private int postsId;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private Users user;

    @Column(name = "question_number")
    private int questionNumber;

    @Column(name = "draft_text", columnDefinition = "TEXT")
    private String draftText;

    @Column(name = "gpt_text", columnDefinition = "TEXT")
    private String gptText;

    @Column(name = "final_text", columnDefinition = "TEXT")
    private String finalText;

    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @Column(name = "is_public", columnDefinition = "VARCHAR(255) DEFAULT 'N'")
    private String isPublic;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "posts", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Likes> likes;

    public Posts(int postsId, Users user, int questionNumber, String draftText, String gptText, String finalText, String title, String isPublic, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postsId = postsId;
        this.user = user;
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
