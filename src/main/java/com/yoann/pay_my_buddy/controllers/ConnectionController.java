package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.exception.ConnectionUserException;
import com.yoann.pay_my_buddy.exception.UserAlreadyConnectedException;
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
     * @param request the HTTP request
     * @param model the Spring model
     * @param flashAttribute the flash attribute from previous requests
     * @return the relation view name
     */
    @GetMapping("/relation")
    public String relation(HttpServletRequest request, Model model, @ModelAttribute("flashAttribute") Object flashAttribute) {
        model.addAttribute("user", new User());

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
     * @throws ConnectionUserException if a technical problem occurs during the connection process
     */
    @PostMapping("/relation")
    public String addRelation(@Valid @ModelAttribute final ConnectionUserForm form, Errors errors, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) throws ConnectionUserException {
        log.info("Post /relation Add connection with user by email");

        if (errors.hasErrors()) {
            log.error("Post /relation {}", errors.getAllErrors());
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "Form is invalid");
            return "redirect:/relation";
        }

        if (!ValidationUtils.emailIsValid(form.getEmail())) {
            log.error("Post /relation Email is invalid");
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "Email is invalid");
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
        } catch (UserAlreadyConnectedException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "User already connected");
            log.error("Post /relation {}","User already connected");
        } catch (ConnectionUserException e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "handle connection failed");
            log.error("Post /relation {}", "handle connection failed");
        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("message_type", "danger");
            redirectAttributes.addFlashAttribute("message", "system error");
            log.error("Post /relation {}","system error");
        }

        return "redirect:/relation";
    }
}
