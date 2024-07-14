package syu.autobiography.spring.login.controller;// AdminController.java

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import syu.autobiography.spring.entity.Posts;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.login.service.PostService;
import syu.autobiography.spring.login.service.UserService;

import java.util.List;
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PostService postService;

    public AdminController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/admin-user")
    public String adminUserPage(Model model) {
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users", users);

        // posts 데이터도 추가로 전달
        List<Posts> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

        return "adminpage/adminpage";
    }

    @GetMapping("/admin-post")
    public String adminPostPage(Model model) {
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users", users);

        // posts 데이터도 추가로 전달
        List<Posts> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

        return "adminpage/adminpage";
    }

    // 사용자 삭제 처리
    @PostMapping("/deleteUser/{userNo}")
    public String deleteUser(@PathVariable("userNo") int userNo) {
        userService.deleteUser(userNo);
        return "redirect:/admin/admin-user";
    }

    @PostMapping("/deletePost/{postId}")
    public String deletePost(@PathVariable("postId") int postId) {
        postService.deletePost(postId);
        return "redirect:/admin/admin-post";
    }


}
