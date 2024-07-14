package syu.autobiography.spring.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syu.autobiography.spring.entity.Posts;

public interface PostRepository extends JpaRepository<Posts, Integer> {
}
