package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.exception.BadProfileDataException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.forms.ProfileForm;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for displaying and updating the user profile.
 */
@Controller
public class ProfileController {
    private final Logger log = LogManager.getLogger(ProfileController.class);

    @Autowired
    private UserService userService;

    /**
     * Displays the profile page with the current user's information.
     *
     * @param user the authenticated user
     * @param model the Spring model
     * @return the profile view
     * @throws UserNotFoundException if the user is not found in the database
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails user, Model model) throws UserNotFoundException {
        log.info("Get /profile");
        User authUser = userService.getUserByEmail(user.getUsername());
        model.addAttribute("user", authUser);
        model.addAttribute("form", new ProfileForm(authUser.getUsername(), authUser.getEmail()));

        return "profile";
    }

    /**
     * Handles the submission of the profile update form.
     * <p>
     * Validates the submitted {@link ProfileForm}, updates the authenticated user's profile
     * if validation passes, and redirects to a logout trigger endpoint to refresh the session.
     * Displays appropriate flash messages in case of errors or invalid data.
     * </p>
     *
     * @param form               the form data containing the updated user profile
     * @param errors             the validation result for the form
     * @param userDetails        the currently authenticated user's details
     * @param redirectAttributes attributes used to pass flash messages on redirect
     * @return a redirect string to either the logout trigger endpoint or back to the profile form on error
     */
    @PostMapping("/profile")
    public String updateProfile(@Validated ProfileForm form, Errors errors, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        log.info("Post /profile");

        try {
            if (errors.hasErrors()) {
                throw new BadProfileDataException();
            }

            User authUser = userService.getUserByEmail(userDetails.getUsername());
            authUser = userService.profileFormToUser(authUser, form);
            userService.updateUser(authUser);

            log.info("Post /profile Update profile successfully");
            return "redirect:/trigger-logout";
        } catch (NullPointerException e) {
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "User not found");

            log.error("Update /profile User to update not found");
        } catch (BadProfileDataException e) {
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "Invalid username, email or password");

            log.error("Update /profile Update user faile because bad profile data");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("message_type", "warning");
            redirectAttributes.addFlashAttribute("message", "User not found");

            log.error("Update /profile User not found");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message_type", "danger");
            redirectAttributes.addFlashAttribute("message", "System error");

            log.error("Update /profile System error");
        }

        log.info("Update /profile Redirect to get /profile");

        return "redirect:/profile";
    }
}
