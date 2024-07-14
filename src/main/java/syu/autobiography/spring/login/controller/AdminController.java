package syu.autobiography.spring.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.entity.Posts;
import syu.autobiography.spring.login.service.UserService;
import syu.autobiography.spring.login.service.PostService;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;
    private final PostService postService;

    public AdminController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/admin/admin-user")
    public String adminPage(Model model) {
        List<Users> users = userService.getAllUsers();
        List<Posts> posts = postService.getAllPosts();
        model.addAttribute("users", users);
        model.addAttribute("posts", posts);
        return "adminpage/adminpage";
    }
}
