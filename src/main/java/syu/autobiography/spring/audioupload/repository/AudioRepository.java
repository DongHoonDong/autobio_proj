package syu.autobiography.spring.audioupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import syu.autobiography.spring.entity.Posts;

import java.util.List;

public interface AudioRepository extends JpaRepository<Posts, Integer> {

    @Query("SELECT p FROM Posts p WHERE p.user.userNo = :userNo ORDER BY p.createdAt DESC")
    List<Posts> getLatestPostsForAllQuestions(@Param("userNo") int userNo);

    List<Posts> findAllByOrderByQuestionNumberAsc();
}
