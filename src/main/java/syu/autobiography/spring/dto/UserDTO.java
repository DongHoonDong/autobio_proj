package syu.autobiography.spring.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDTO {
    private int userNo;

    private String userId;

    private String userPwd;

    private String userName;

    private String userPhone;

    private String userBirth;

    private String userGender;

    private String userRole;

    public UserDTO(Integer userNo, String userId, String userPwd, String userName, String userPhone, String userBirth, String userGender, String userRole) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userBirth = userBirth;
        this.userGender = userGender;
        this.userRole = userRole;
    }
}