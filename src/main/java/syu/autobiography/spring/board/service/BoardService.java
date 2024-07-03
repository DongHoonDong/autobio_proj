package syu.autobiography.spring.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import syu.autobiography.spring.board.repository.BoardRepository;
import syu.autobiography.spring.dto.DraftDTO;
import syu.autobiography.spring.entity.Drafts;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public List<DraftDTO> getTopStories(int limit) {
        return boardRepository.findAllWithUsers(PageRequest.of(0, limit)).getContent().stream()
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

    public Page<DraftDTO> getAllDrafts(PageRequest pageRequest) {
        return boardRepository.findAllWithUsers(pageRequest).map(draft -> new DraftDTO(
                draft.getDraftNo(),
                draft.getUsers().getUserNo(),
                draft.getUsers().getUserName(),
                calculateAge(draft.getUsers().getUserBirth()),
                draft.getRequestTime(),
                draft.getDraftContent(),
                draft.getResponseContent(),
                draft.getChapterNumber()
        ));
    }

    private int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
