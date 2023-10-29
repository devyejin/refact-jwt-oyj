package com.example.usedtrade.domain.user.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@Log4j2
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }
}
