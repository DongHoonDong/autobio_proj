package syu.autobiography.spring.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString

//나중에 혹시 모를 추가 기능 구현을 위한 좋아요 DTO 입니다.

public class LikeDTO {
    private int likeId;
    private int userNo;
    private int requireNo;
    private int likeCount;

    public LikeDTO(int likeId, int userNo, int requireNo, int likeCount) {
        this.likeId = likeId;
        this.userNo = userNo;
        this.requireNo = requireNo;
        this.likeCount = likeCount;
    }
}
