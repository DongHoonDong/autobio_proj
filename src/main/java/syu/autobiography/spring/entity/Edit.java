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
@Table(name = "edit")
public class Edit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edit_no")
    private int editNo;

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private Users users;

    @Column(name = "edit_title", nullable = false)
    private String editTitle;

    @Column(name = "edit_text", nullable = false, columnDefinition = "TEXT")
    private String editText;

    @Column(name = "edit_time", nullable = false)
    private LocalDateTime editTime;

    @Column(name = "chapter_number", nullable = false)
    private int chapterNumber;

    public Edit(int editNo, Users users, String editTitle, String editText, LocalDateTime editTime, int chapterNumber) {
        this.editNo = editNo;
        this.users = users;
        this.editTitle = editTitle;
        this.editText = editText;
        this.editTime = editTime;
        this.chapterNumber = chapterNumber;
    }
}
