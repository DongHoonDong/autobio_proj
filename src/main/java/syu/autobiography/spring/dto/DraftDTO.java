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
public class DraftDTO {
    private int draftNo;
    private int userNo;
    private String userName;
    private int userAge;
    private LocalDateTime requestTime;
    private String draftContent;
    private String responseContent;
    private int chapterNumber;

    public DraftDTO(int draftNo, int userNo, String userName, int userAge, LocalDateTime requestTime, String draftContent, String responseContent, int chapterNumber) {
        this.draftNo = draftNo;
        this.userNo = userNo;
        this.userName = userName;
        this.userAge = userAge;
        this.requestTime = requestTime;
        this.draftContent = draftContent;
        this.responseContent = responseContent;
        this.chapterNumber = chapterNumber;
    }
}
