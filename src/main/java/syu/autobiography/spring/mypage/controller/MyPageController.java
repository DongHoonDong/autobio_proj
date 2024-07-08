package syu.autobiography.spring.mypage.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import syu.autobiography.spring.dto.PostsDTO;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.login.service.UserService;
import syu.autobiography.spring.mypage.service.MyPageService;

import java.util.List;

@Controller
public class MyPageController {

    private final MyPageService myPageService;
    private final UserService userService;

    @Autowired
    public MyPageController(MyPageService myPageService, UserService userService) {
        this.myPageService = myPageService;
        this.userService = userService;
    }

    @GetMapping("/mypage")
    public String getMyPage(Model model, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<PostsDTO> myPosts = myPageService.getPostsByUser(currentUser.getUserNo());
        model.addAttribute("user", currentUser);
        model.addAttribute("myPosts", myPosts);

        return "userpage/mypage";
    }

    @PostMapping("/toggleVisibility")
    public String toggleVisibility(@RequestParam int postsId, @RequestParam String isPublic) {
        myPageService.updatePostVisibility(postsId, isPublic);
        return "redirect:/mypage";
    }

    @GetMapping("/likelist")
    public String getLikedPosts(Model model, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<PostsDTO> likedPosts = myPageService.getLikedPostsByUser(currentUser.getUserNo());
        model.addAttribute("user", currentUser);
        model.addAttribute("likedPosts", likedPosts);

        return "userpage/likelist";
    }

    @GetMapping("/myinfo")
    public String getMyInfo(Model model, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        return "userpage/myinfo";
    }

    @PostMapping("/myinfo")
    public String updateMyInfo(@RequestParam String userName,
                               @RequestParam String userPhone,
                               @RequestParam(required = false) String currentPwd,
                               @RequestParam(required = false) String newPwd,
                               @RequestParam(required = false) String confirmPwd,
                               Model model,
                               HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        currentUser.setUserName(userName);

        // 전화번호 형식 확인
        String phonePattern = "^\\d{3}-\\d{3,4}-\\d{4}$";
        if (!userPhone.matches(phonePattern)) {
            model.addAttribute("error", "전화번호 형식이 올바르지 않습니다. 올바른 형식: 010-1234-5678");
            model.addAttribute("user", currentUser);
            return "userpage/myinfo";
        }
        currentUser.setUserPhone(userPhone);

        // 비밀번호 변경
        if (currentPwd != null && !currentPwd.isEmpty() && newPwd != null && !newPwd.isEmpty() && confirmPwd != null && !confirmPwd.isEmpty()) {
            if (!currentUser.getUserPwd().equals(currentPwd)) {
                model.addAttribute("error", "현재 비밀번호가 일치하지 않습니다.");
                model.addAttribute("user", currentUser);
                return "userpage/myinfo";
            }
            if (!newPwd.equals(confirmPwd)) {
                model.addAttribute("error", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
                model.addAttribute("user", currentUser);
                return "userpage/myinfo";
            }
            currentUser.setUserPwd(newPwd);
        }

        userService.updateUser(currentUser);
        session.setAttribute("user", currentUser);

        return "redirect:/myinfo";
    }

    @GetMapping("/delete-account")
    public String getDeleteAccountPage(HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        return "userpage/goodbye";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(@RequestParam String password, HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (!currentUser.getUserPwd().equals(password)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "userpage/goodbye";
        }

        userService.deleteUser(currentUser.getUserNo());
        session.invalidate();

        return "redirect:/delete-success";
    }

    @PostMapping("/delete-post")
    public String deletePost(@RequestParam int postId, HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        myPageService.deletePost(postId);
        return "redirect:/mypage";
    }

    @GetMapping("/delete-success")
    public String getDeleteSuccessPage() {
        return "userpage/delete-success";
    }
}
