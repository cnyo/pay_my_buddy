package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.enums.PageName;
import com.yoann.pay_my_buddy.exception.ConnectionUserException;
import com.yoann.pay_my_buddy.exception.UserAlreadyConnectedException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.forms.EmailForm;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import com.yoann.pay_my_buddy.utils.ValidationUtils;
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

/**
 * Controller responsible for managing user connections (friend relations).
 */
@Controller
public class ConnectionController {

    private final Logger log = LogManager.getLogger(ConnectionController.class);

    @Autowired
    private UserService userService;

    /**
     * Displays the relation page where the user can add a connection.
     *
     * @param model the Spring model
     * @param flashAttribute the flash attribute from previous requests
     * @return the relation view name
     */
    @GetMapping("/relation")
    public String relation(Model model, @ModelAttribute("flashAttribute") Object flashAttribute) {
        model.addAttribute("user", new User());
        model.addAttribute("page", PageName.RELATION.getPage());

        return "relation";
    }

    /**
     * Handles form submission for adding a new user connection.
     *
     * @param form the submitted form with email
     * @param errors form validation errors
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect messages
     * @return redirect to the relation page
     */
    @PostMapping("/relation")
    public String addRelation(@Valid @ModelAttribute final EmailForm form, Errors errors, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        log.info("Post /relation Add connection with user by email");

        if (errors.hasErrors()) {
            log.error("Post /relation {}", errors.getAllErrors());
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "Form is invalid");
            return "redirect:/relation";
        }

        try {
            User authUser = userService.getUserByEmail(userDetails.getUsername());
            User userToConnect = userService.getUserByEmail(form.getEmail());
            userService.addConnectionToUser(authUser, userToConnect);
            redirectAttributes.addFlashAttribute("message_type", "success");
            redirectAttributes.addFlashAttribute("message", "connection added successfully");

            log.info("Post /relation add connection success");
        } catch (UserNotFoundException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "User not found");
            log.error("Post /relation {}", "User not found");
        } catch (ConnectionUserException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            log.error("Post /relation {}", e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("message_type", "danger");
            redirectAttributes.addFlashAttribute("message", "system error");
            log.error("Post /relation {}","system error");
        }

        return "redirect:/relation";
    }
}
