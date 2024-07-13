package syu.autobiography.spring.write;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import syu.autobiography.spring.audioupload.service.AudioService;
import syu.autobiography.spring.entity.Posts;
import syu.autobiography.spring.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class WriteController {

    @Autowired
    private AudioService audioService;

    @GetMapping("/write")
    public String showWritePage(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Posts draft = audioService.getDraftByUser(user);
        model.addAttribute("draft", draft);
        return "write/write";
    }

    @PostMapping("/write")
    public String submitFinalDraft(@RequestParam("finalText") String finalText,
                                   @RequestParam("title") String title,
                                   HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        audioService.updateFinalDraftAndTitle(finalText, title, user);
        return "redirect:/mypage";
    }
}