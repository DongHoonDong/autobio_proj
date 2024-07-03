package syu.autobiography.spring.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syu.autobiography.spring.entity.Drafts;

public interface BoardRepository extends JpaRepository<Drafts, Integer> {

    @Query("SELECT d FROM Drafts d JOIN FETCH d.users")
    Page<Drafts> findAllWithUsers(Pageable pageable);
}
