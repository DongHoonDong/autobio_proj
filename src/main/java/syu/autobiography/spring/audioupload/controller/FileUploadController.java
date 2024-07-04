package syu.autobiography.spring.audioupload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileUploadController {

    @GetMapping("/question1")
    public String question1Page() {
        return "fileupload/question1";
    }

    @GetMapping("/question2")
    public String question2Page() {
        return "fileupload/question2";
    }

    @GetMapping("/question3")
    public String question3Page() {
        return "fileupload/question3";
    }

    @GetMapping("/question4")
    public String question4Page() {
        return "fileupload/question4";
    }

    @GetMapping("/question5")
    public String question5Page() {
        return "fileupload/question5";
    }
    }

