package syu.autobiography.spring.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import syu.autobiography.spring.board.repository.BoardRepository;
import syu.autobiography.spring.dto.DraftDTO;
import syu.autobiography.spring.entity.Drafts;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public List<DraftDTO> getTopStories(int limit) {
        return boardRepository.findAllWithUsers().stream()
                .limit(limit)
                .map(draft -> new DraftDTO(
                        draft.getDraftNo(),
                        draft.getUsers().getUserNo(),
                        draft.getUsers().getUserName(),
                        calculateAge(draft.getUsers().getUserBirth()),
                        draft.getRequestTime(),
                        draft.getDraftContent(),
                        draft.getResponseContent(),
                        draft.getChapterNumber()
                ))
                .collect(Collectors.toList());
    }

    public List<DraftDTO> getAllDrafts() {
        return boardRepository.findAllWithUsers().stream()
                .map(draft -> new DraftDTO(
                        draft.getDraftNo(),
                        draft.getUsers().getUserNo(),
                        draft.getUsers().getUserName(),
                        calculateAge(draft.getUsers().getUserBirth()),
                        draft.getRequestTime(),
                        draft.getDraftContent(),
                        draft.getResponseContent(),
                        draft.getChapterNumber()
                ))
                .collect(Collectors.toList());
    }

    private int calculateAge(LocalDate birthDate) {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}
