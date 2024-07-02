package syu.autobiography.spring.audioupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syu.autobiography.spring.entity.Drafts;

public interface AudioRepository extends JpaRepository<Drafts, Long> {
}
