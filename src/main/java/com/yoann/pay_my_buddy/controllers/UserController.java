package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String home(Model model) {
        Iterable<User> users = userService.getUsers();
        model.addAttribute("users", users);

        return "home";
    }
}
