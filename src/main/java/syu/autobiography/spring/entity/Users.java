package syu.autobiography.spring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private int userNo;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "user_pwd", nullable = false)
    private String userPwd;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_birth")
    private String userBirth;

    @Column(name = "user_phone")
    private String userPhone;

    @Column(name = "user_gender")
    private String userGender;

    @Column(name = "user_role")
    private String userRole;

    public Users(int userNo, String userId, String userPwd, String userName, String userBirth, String userPhone, String userGender, String userRole) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPwd = userPwd;
        this.userName = userName;
        this.userBirth = userBirth;
        this.userPhone = userPhone;
        this.userGender = userGender;
        this.userRole = userRole;
    }
}