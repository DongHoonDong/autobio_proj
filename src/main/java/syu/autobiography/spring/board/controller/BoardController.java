package syu.autobiography.spring.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        return "index";
    }

    @GetMapping("/board/post_story")
    public String getStories(@RequestParam(value = "page", defaultValue = "1") int pageNumber, Model model) {
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        Page<DraftDTO> page = boardService.getAllDrafts(pageRequest);

        model.addAttribute("stories", page.getContent());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());

        return "board/post_story";
    }
}
