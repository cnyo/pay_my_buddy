package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.exception.BadRegistrationDataException;
import com.yoann.pay_my_buddy.forms.RegistrationForm;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {
    private final Logger log = LogManager.getLogger(RegistrationController.class);

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("form", new RegistrationForm());

        return "registration";
    }

    @PostMapping("/registration")
    public String saveRegistration(@Validated RegistrationForm form, Errors errors, RedirectAttributes redirectAttributes) {

        try {
            if (errors.hasErrors()) {
                throw new BadRegistrationDataException(errors.getAllErrors().toString());
            }

            log.info("Post /registration Create an account");
            User user = userService.initUserFromRegistrationForm(form);
            user = userService.addUser(user);

            redirectAttributes.addFlashAttribute("messageType", "success");
            redirectAttributes.addFlashAttribute("message", "Registration success !");
            redirectAttributes.addFlashAttribute("user", user);

            return "redirect:/login?registrationSuccess";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "redirect:/registration?error";
        }

    }
}
