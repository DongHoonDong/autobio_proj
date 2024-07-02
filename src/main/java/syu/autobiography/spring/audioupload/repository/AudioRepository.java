package syu.autobiography.spring.audioupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import syu.autobiography.spring.entity.Drafts;

import java.util.List;

public interface AudioRepository extends JpaRepository<Drafts, Long> {
    List<Drafts> findAllByOrderByChapterNumberAsc();

    @Query("SELECT d FROM Drafts d WHERE d.requestTime = (SELECT MAX(d2.requestTime) FROM Drafts d2 WHERE d2.chapterNumber = d.chapterNumber) AND d.chapterNumber BETWEEN 1 AND 5 ORDER BY d.chapterNumber")
    List<Drafts> getLatestDraftsForAllChapters();
}