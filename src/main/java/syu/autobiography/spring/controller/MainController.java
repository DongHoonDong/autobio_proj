package syu.autobiography.spring.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class MainController {
    @GetMapping(value={"/main"})
    public String main() {return "index";}

}
