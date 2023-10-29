package com.example.usedtrade.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/sample")
public class SampleController {

    @GetMapping("/test")
    public List<String> test() {
        return Arrays.asList("SUCCESS","SWAGGER","TEST");
    }
}
