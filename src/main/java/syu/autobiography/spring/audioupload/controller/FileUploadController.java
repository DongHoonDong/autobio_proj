package syu.autobiography.spring.audioupload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileUploadController {

    @GetMapping("/chapter1")
    public String chapter1Page() {
        return "fileupload/chapter1";
    }

    @GetMapping("/chapter2")
    public String chapter2Page() {
        return "fileupload/chapter2";
    }

    @GetMapping("/chapter3")
    public String chapter3Page() {
        return "fileupload/chapter3";
    }

    @GetMapping("/chapter4")
    public String chapter4Page() {
        return "fileupload/chapter4";
    }

    @GetMapping("/chapter5")
    public String chapter5Page() {
        return "fileupload/chapter5";
    }
}