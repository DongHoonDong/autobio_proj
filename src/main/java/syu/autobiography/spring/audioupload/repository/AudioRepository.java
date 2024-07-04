package syu.autobiography.spring.audioupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syu.autobiography.spring.entity.Posts;

import java.util.List;

public interface AudioRepository extends JpaRepository<Posts, Long> {
    List<Posts> findAllByOrderByQuestionNumberAsc();

    @Query("SELECT p FROM Posts p WHERE p.createdAt = (SELECT MAX(p2.createdAt) FROM Posts p2 WHERE p2.questionNumber = p.questionNumber) AND p.questionNumber BETWEEN 1 AND 5 ORDER BY p.questionNumber")
    List<Posts> getLatestPostsForAllQuestions();
}