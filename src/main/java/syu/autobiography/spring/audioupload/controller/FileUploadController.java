package syu.autobiography.spring.audioupload.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FileUploadController {

    @GetMapping("/fileupload")
    public String fileUploadForm() {
        return "fileupload/fileupload";
    }
}
