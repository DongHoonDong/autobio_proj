package syu.autobiography.spring.board.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import syu.autobiography.spring.board.service.BoardService;
import syu.autobiography.spring.dto.PostsDTO;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.board.service.BoardService.LikeResponse;

import java.util.List;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String getMainPage(Model model, HttpSession session) {
        List<PostsDTO> stories = boardService.getTopStories(10, session);
        model.addAttribute("stories", stories);
        return "index";
    }

    @GetMapping("/board/post_story")
    public String getStories(@RequestParam(value = "page", defaultValue = "1") int pageNumber, Model model, HttpSession session) {
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        Page<PostsDTO> page = boardService.getAllDrafts(pageRequest, session);

        model.addAttribute("stories", page.getContent());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());

        return "board/post_story";
    }

    @PostMapping("/toggleLike")
    @ResponseBody
    public LikeResponse toggleLike(@RequestParam(name = "userNo") int userNo,
                                   @RequestParam(name = "postsId") int postsId,
                                   HttpSession session) {        Users currentUser = (Users) session.getAttribute("user");
        if (currentUser == null) {
            return null;
        }
        return boardService.toggleLike(userNo, postsId);
    }
}
