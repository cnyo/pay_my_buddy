package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.exception.BadProfileDataException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.forms.ProfileForm;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails user, Model model) throws UserNotFoundException {
        User authUser = userService.getUserByUsername(user.getUsername());
        model.addAttribute("user", authUser);
        model.addAttribute("form", new ProfileForm(authUser.getUsername(), authUser.getEmail()));

        return "profile";
    }

    @PutMapping("/profile")
    public String updateProfile(@Validated ProfileForm form, Errors error, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            if (error.hasErrors()) {
                throw new BadProfileDataException();
            }

            User authUser = userService.getUserByUsername(userDetails.getUsername());
            userService.updateUserFromProfileForm(authUser.getId(), form);
            redirectAttributes.addFlashAttribute("message_type", "success");
            redirectAttributes.addFlashAttribute("message", "User updated successfully");
        } catch (NullPointerException e) {
            redirectAttributes.addFlashAttribute("message_type", "error");
            redirectAttributes.addFlashAttribute("message", "User not found");
        } catch (BadProfileDataException e) {
            redirectAttributes.addFlashAttribute("message_type", "error");
            redirectAttributes.addFlashAttribute("message", "Invalid username or email or password");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message_type", "error");
            redirectAttributes.addFlashAttribute("message", "System error");
        }

        return "redirect:/profile";
    }
}
