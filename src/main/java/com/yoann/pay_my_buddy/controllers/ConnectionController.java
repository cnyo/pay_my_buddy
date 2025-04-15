package com.yoann.pay_my_buddy.controllers;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ConnectionController {

    private final Logger log = LogManager.getLogger(TransactionController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/relation")
    public String relation(Model model) {
        model.addAttribute("user", new User());

        return "relation";
    }

    @PostMapping("/relation")
    public RedirectView addRelation(@RequestBody String email) throws InvalidTypeIdException {
        try {
            log.info("add relation to user email: {}", email);
            User authUser = userService.getUser(1L);
            User userToConnect = userService.getUser(3L);
//            User userToConnect = userService.getUserByEmail(user.getEmail());
            userService.addConnectionToUser(authUser, userToConnect);
            // todo: setter un message flash
        } catch (InvalidTypeIdException e) {
            log.error(e.getMessage());
            // todo: setter un message flash
        }

//        return "redirect:/relation";
        return new RedirectView("relation", true);
    }
}
