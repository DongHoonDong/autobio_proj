package syu.autobiography.spring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "drafts")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Drafts implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "draft_no")
    private int draftNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private Users users;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "draft_content", nullable = false)
    private String draftContent;

    @Column(name = "response_content", nullable = false)
    private String responseContent;

    @Column(name = "chapter_number", nullable = false)
    private int chapterNumber;

    public Drafts(Users users, LocalDateTime requestTime, String draftContent, String responseContent, int chapterNumber) {
        this.users = users;
        this.requestTime = requestTime;
        this.draftContent = draftContent;
        this.responseContent = responseContent;
        this.chapterNumber = chapterNumber;
    }
}
