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
@Table(name = "drafts")
public class Drafts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "draft_no")
    private int draftNo;

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private Users users;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "draft_content", nullable = false, columnDefinition = "TEXT")
    private String draftContent;

    @Column(name = "response_content", nullable = true, columnDefinition = "TEXT")
    private String responseContent;

    @Column(name = "chapter_number", nullable = false)
    private int chapterNumber;

    public Drafts(int draftNo, Users users, LocalDateTime requestTime, String draftContent, String responseContent, int chapterNumber) {
        this.draftNo = draftNo;
        this.users = users;
        this.requestTime = requestTime;
        this.draftContent = draftContent;
        this.responseContent = responseContent;
        this.chapterNumber = chapterNumber;
    }
}
