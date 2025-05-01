package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.exception.ConnectionUserException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.forms.ConnectionUserForm;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import com.yoann.pay_my_buddy.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ConnectionController {

    private final Logger log = LogManager.getLogger(ConnectionController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/relation")
    public String relation(HttpServletRequest request, Model model, @ModelAttribute("flashAttribute") Object flashAttribute) {
        model.addAttribute("user", new User());

        return "relation";
    }

    @PostMapping("/relation")
    public String addRelation(@Valid @ModelAttribute final ConnectionUserForm form, Errors errors, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) throws ConnectionUserException {
        log.info("Post /relation Add connection with user by email");

        if (errors.hasErrors()) {
            log.error("Post /relation {}", errors.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "Errors in relation");
            return "redirect:/relation";
        }

        if (!ValidationUtils.emailIsValid(form.getEmail())) {
            log.error("Post /relation Email is invalid");
            redirectAttributes.addFlashAttribute("error", "Email is invalid");
            return "redirect:/relation";
        }

        try {
            User authUser = userService.getUserByEmail(userDetails.getUsername());
            User userToConnect = userService.getUserByEmail(form.getEmail());
            userService.addConnectionToUser(authUser, userToConnect);
            redirectAttributes.addFlashAttribute("success", "connection added successfully");
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "User not found");
        } catch (ConnectionUserException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "handle connection failed");
        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "system error");
        }

        return "redirect:/relation";
    }
}
