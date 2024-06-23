package syu.autobiography.spring.login.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import syu.autobiography.spring.dto.UserDTO;
import syu.autobiography.spring.entity.Users;
import syu.autobiography.spring.login.service.UserService;

@Controller
public class LoginController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(UserDTO userDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = userService.login(userDTO);

        if (user == null) {
            redirectAttributes.addFlashAttribute("loginError", "회원정보가 일치하지 않습니다.");
            return "redirect:/login";
        }

        session.setAttribute("user", user);

        if ("Y".equals(user.getUserRole())) {
            return "redirect:/admin/admin-user";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(UserDTO userDTO, Model model) {
        logger.info("Received registration request for user: {}", userDTO.getUserId());
        try {
            userService.register(userDTO);
            logger.info("User registered successfully: {}", userDTO.getUserId());
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error during registration: ", e);
            model.addAttribute("registerError", e.getMessage());
            return "register";
        }
    }
}