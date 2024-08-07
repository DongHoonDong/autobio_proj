package syu.autobiography.spring.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syu.autobiography.spring.entity.Posts;

public interface BoardRepository extends JpaRepository<Posts, Integer> {

    @Query("SELECT p FROM Posts p LEFT JOIN p.user u LEFT JOIN p.likes l WHERE p.isPublic = 'Y' GROUP BY p.postsId ORDER BY COUNT(l) DESC")
    Page<Posts> findAllPublicWithUsersAndLikesOrderByLikesCountDesc(Pageable pageable);
}
