package com.vaibhav.chatapp.chatapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiRouterController {
    @GetMapping("/check")
    public String index() {
        return "Hello World";
    }
}
