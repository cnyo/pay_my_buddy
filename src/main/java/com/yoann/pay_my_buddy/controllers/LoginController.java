package com.yoann.pay_my_buddy.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @GetMapping("/user")
    public String getUser() {
        return "Hello user";
    }

    @GetMapping("/admin")
    public String getAdmin() {
        return "Hello admin";
    }
}
