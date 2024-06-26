package syu.autobiography.spring.audioupload.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import syu.autobiography.spring.dto.DraftDTO;
import syu.autobiography.spring.entity.Drafts;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.audioupload.repository.AudioRepository;

import java.time.LocalDateTime;

@Service
public class AudioService {

    @Autowired
    private AudioRepository audioRepository;

    public void saveDraft(String transcript, String guideline) {

        Users user = new Users(); // 임의로 사용자 생성
        user.setUserNo(1); // 임의로 1로 설정

        Drafts draft = new Drafts();
        draft.setUsers(user);
        draft.setRequestTime(LocalDateTime.now());
        draft.setDraftContent(guideline);
        draft.setResponseContent(transcript);
        draft.setChapterNumber(1); // 임의로 1으로 설정

        audioRepository.save(draft);
    }
}
