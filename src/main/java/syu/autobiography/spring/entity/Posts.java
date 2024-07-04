package syu.autobiography.spring.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter @Setter @NoArgsConstructor @ToString @EqualsAndHashCode
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postsId;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private Users user;

    private int questionNumber;

    @Column(columnDefinition = "TEXT")
    private String draftText;

    @Column(columnDefinition = "TEXT")
    private String gptText;

    @Column(columnDefinition = "TEXT")
    private String finalText;

    private String title;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Posts(int postsId, Users user, int questionNumber, String draftText, String gptText, String finalText, String title, boolean isPublic, LocalDateTime createdAt, LocalDateTime updatedAt) {
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