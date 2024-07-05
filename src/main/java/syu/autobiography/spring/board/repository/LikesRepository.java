package syu.autobiography.spring.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import syu.autobiography.spring.entity.Likes;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Integer> {
    Optional<Likes> findByUserUserNoAndPostsPostsId(int userNo, int postsId);

    @Query("SELECT COUNT(l) FROM Likes l WHERE l.posts.postsId = :postsId")
    int countByPostsId(@Param("postsId") int postsId);
}
