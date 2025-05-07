package com.yoann.pay_my_buddy.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for authentication-related endpoints.
 */
@Controller
public class AuthController {
    private final Logger log = LogManager.getLogger(AuthController.class);

    /**
     * Displays the login page.
     *
     * @return the name of the login view
     */
    @GetMapping("/login")
    public String login() {
        log.info("Get login page");
        return "login";
    }

    /**
     * Handles GET requests to trigger the logout process.
     * <p>
     * This method returns the name of a view (e.g., an HTML page) that contains
     * a form to perform a POST request to the "/logout" endpoint, which is
     * required by Spring Security to properly log out the user.
     * </p>
     *
     * @return the name of the logout view (e.g., a page containing a logout form)
     */
    @GetMapping("/trigger-logout")
    public String triggerLogout() {
        return "logout";
    }
}
