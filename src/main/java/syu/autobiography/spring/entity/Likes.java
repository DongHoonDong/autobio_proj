package syu.autobiography.spring.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode

//나중에 혹시 모를 추가 기능 구현을 위한 좋아요 엔티티 입니다.

public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private int likeId;

    @ManyToOne
    @JoinColumn(name = "user_no", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "edit_no", nullable = false)
    private Edit edit;
}
