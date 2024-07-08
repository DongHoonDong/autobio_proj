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
public class EditsDTO {
    private int editNo;
    private int user_no;
    private String editTitle;
    private String editText;
    private LocalDateTime editTime;
    private int chapterNumber;

    public EditsDTO(int editNo, int user_no, String editTitle, String editText, LocalDateTime editTime, int chapterNumber) {
        this.editNo = editNo;
        this.user_no = user_no;
        this.editTitle = editTitle;
        this.editText = editText;
        this.editTime = editTime;
        this.chapterNumber = chapterNumber;
    }
}
