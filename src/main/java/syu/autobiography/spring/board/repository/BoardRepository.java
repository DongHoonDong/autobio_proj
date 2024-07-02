package syu.autobiography.spring.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syu.autobiography.spring.entity.Drafts;

import java.util.List;

public interface BoardRepository extends JpaRepository<Drafts, Integer> {

    @Query("SELECT d FROM Drafts d JOIN FETCH d.users")
    List<Drafts> findAllWithUsers();
}
