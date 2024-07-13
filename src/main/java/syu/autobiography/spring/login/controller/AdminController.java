package syu.autobiography.spring.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.login.service.UserService;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/admin-user")
    public String adminPage(Model model) {
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "adminpage/adminpage";
    }
}
