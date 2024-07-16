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

    // 마이페이지
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

    // 마이페이지 정보수정
    @GetMapping("/edit")
    public String getDrafts(Model model, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        List<PostsDTO> draftPosts = myPageService.getDraftsByUser(currentUser.getUserNo());
        model.addAttribute("user", currentUser);
        model.addAttribute("draftPosts", draftPosts);

        return "userpage/editpost";
    }

    // 글삭제
    @PostMapping("/delete-draft")
    public String deleteDraft(@RequestParam(name = "postId") int postId, HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        myPageService.deletePost(postId);
        return "redirect:/edit";
    }

    // 비공개-공개처리
    @PostMapping("/toggleVisibility")
    public String toggleVisibility(@RequestParam(name = "postsId") int postsId, @RequestParam(name = "isPublic") String isPublic) {
        myPageService.updatePostVisibility(postsId, isPublic);
        return "redirect:/mypage";
    }

    // 좋아요목록
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

    // 내정보
    @GetMapping("/myinfo")
    public String getMyInfo(Model model, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        return "userpage/myinfo";
    }

    // 내정보 수정
    @PostMapping("/myinfo")
    public String updateMyInfo(@RequestParam(name = "userName") String userName,
                               @RequestParam(name = "userPhone") String userPhone,
                               @RequestParam(name = "currentPwd", required = false) String currentPwd,
                               @RequestParam(name = "newPwd", required = false) String newPwd,
                               @RequestParam(name = "confirmPwd", required = false) String confirmPwd,
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

    // 탈퇴 페이지
    @GetMapping("/delete-account")
    public String getDeleteAccountPage(HttpSession session, Model model) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", currentUser);
        return "userpage/goodbye";
    }

    // 탈퇴 처리
    @PostMapping("/delete-account")
    public String deleteAccount(@RequestParam(name = "password") String password, HttpSession session, Model model) {
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

    // 글 삭제
    @PostMapping("/delete-post")
    public String deletePost(@RequestParam(name = "postId") int postId, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        myPageService.deletePost(postId);
        return "redirect:/mypage";
    }

    // 탈퇴 완료 페이지
    @GetMapping("/delete-success")
    public String getDeleteSuccessPage() {
        return "userpage/delete-success";
    }

    // 포스트 수정 페이지
    @GetMapping("/edit-post")
    public String getEditPostPage(@RequestParam(name = "postsId") int postsId, Model model, HttpSession session) {
        Users currentUser = (Users) session.getAttribute("user");

        if (currentUser == null) {
            return "redirect:/login";
        }

        PostsDTO postDTO = myPageService.getPostById(postsId);
        model.addAttribute("user", currentUser);
        model.addAttribute("post", postDTO);

        return "userpage/editpost";
    }

    // 포스트 수정 처리
    @PostMapping("/edit-post")
    public String editPost(@RequestParam(name = "postsId") int postsId,
                           @RequestParam(name = "title") String title,
                           @RequestParam(name = "finalText") String finalText) {
        myPageService.updatePost(postsId, title, finalText);
        return "redirect:/mypage";
    }
}
