package syu.autobiography.spring.mypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syu.autobiography.spring.entity.Posts;

import java.util.List;

public interface MyPageRepository extends JpaRepository<Posts, Integer> {
    List<Posts> findByUserUserNo(int userNo);

    @Query("SELECT p FROM Posts p JOIN p.likes l WHERE l.user.userNo = :userNo")
    List<Posts> findLikedPostsByUser(int userNo);
}
