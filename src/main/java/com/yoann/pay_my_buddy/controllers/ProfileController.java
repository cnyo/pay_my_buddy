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
import org.springframework.web.bind.annotation.PutMapping;
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
     * Updates the user profile based on the submitted form.
     *
     * @param form the profile form
     * @param errors validation errors
     * @param userDetails the authenticated user
     * @param redirectAttributes attributes for redirect messages
     * @return redirect to the profile page
     */
    @PostMapping("/profile")
    public String updateProfile(@Validated ProfileForm form, Errors errors, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        log.info("Update /profile");

        try {
            if (errors.hasErrors()) {
                throw new BadProfileDataException();
            }

            User authUser = userService.getUserByEmail(userDetails.getUsername());
            User updatedUser = userService.profileFormToUser(authUser, form);
            userService.updateUser(updatedUser);
            redirectAttributes.addFlashAttribute("message_type", "success");
            redirectAttributes.addFlashAttribute("message", "User updated successfully");

            log.info("Update /profile Update profile successfully");
            return "redirect:/profile?success";
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
