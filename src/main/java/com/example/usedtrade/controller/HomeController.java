package com.example.usedtrade.controller;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@Log4j2
public class HomeController {

    @GetMapping("/")
    public String home(Principal principal, Model model){
        log.info(" get '/' 호출 ! ");

        if(principal != null) {
            model.addAttribute("principal", principal);
        }

        return "index";
    }
}
