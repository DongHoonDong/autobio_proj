package syu.autobiography.spring.login.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
        return "user/login";
    }

    @PostMapping("/login")
    public String login(UserDTO userDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        Users user = userService.login(userDTO);

        if (user == null) {
            redirectAttributes.addFlashAttribute("loginError", "회원정보가 일치하지 않습니다.");
            return "redirect:/login";
        }

        session.setAttribute("user", user);
        logger.info("User {} logged in and stored in session", user.getUserId());

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
        return "user/register";
    }

    @PostMapping("/register")
    public String register(UserDTO userDTO, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Received registration request for user: {}", userDTO.getUserId());
        try {
            userService.register(userDTO);
            logger.info("User registered successfully: {}", userDTO.getUserId());
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error during registration: ", e);
            redirectAttributes.addFlashAttribute("registerError", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/findinfo")
    public String findInfoPage() {
        return "user/findinfo";
    }

    @PostMapping("/findId")
    public String findId(@RequestParam("userName") String userName, @RequestParam("userPhone") String userPhone, Model model) {
        String userId = userService.findUserId(userName, userPhone);
        if (userId != null) {
            model.addAttribute("foundUserId", userId);
        } else {
            model.addAttribute("idError", "일치하는 사용자 정보가 없습니다.");
        }
        return "user/findinfo";
    }

    @PostMapping("/findPassword")
    public String findPassword(@RequestParam("userId") String userId, @RequestParam("userPhone") String userPhone, Model model) {
        Users user = userService.findUserByIdAndPhone(userId, userPhone);
        if (user != null) {
            model.addAttribute("foundUser", user);
        } else {
            model.addAttribute("pwdError", "일치하는 사용자 정보가 없습니다.");
        }
        return "user/findinfo";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("userId") String userId, @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("resetError", "비밀번호가 일치하지 않습니다.");
            return "redirect:/findinfo";
        }
        try {
            userService.updatePassword(userId, newPassword);
            redirectAttributes.addFlashAttribute("resetSuccess", "비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("resetError", "비밀번호 변경 중 오류가 발생했습니다.");
        }
        return "redirect:/login";
    }
    @GetMapping("/checkUserId")
    @ResponseBody
    public ResponseEntity<String> checkUserId(@RequestParam("userId") String userId) {
        boolean exists = userService.existsByUserId(userId);
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용중인 아이디입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 아이디입니다.");
        }
    }
    @GetMapping("/checkUserPhone")
    @ResponseBody
    public ResponseEntity<String> checkUserPhone(@RequestParam("userPhone") String userPhone) {
        boolean exists = userService.existsByUserPhone(userPhone);
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용중인 전화번호입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 전화번호입니다.");
        }
    }


}
