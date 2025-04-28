package com.yoann.pay_my_buddy.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    private final Logger log = LogManager.getLogger(AuthController.class);


    @GetMapping("/login")
    public String login() {
        log.info("Get login page");
        return "login";
    }
}
