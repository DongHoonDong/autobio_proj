package syu.autobiography.spring.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import syu.autobiography.spring.board.service.BoardService;
import syu.autobiography.spring.dto.DraftDTO;

import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String getMainPage(Model model) {
        List<DraftDTO> stories = boardService.getTopStories(6); // 6개의 글만 가져오기
        model.addAttribute("stories", stories);
        return "index"; // index.html 템플릿을 반환
    }

    @GetMapping("/stories")
    public String getStories(Model model) {
        List<DraftDTO> stories = boardService.getAllDrafts();
        model.addAttribute("stories", stories);
        return "stories"; // stories.html 템플릿을 반환
    }
}
