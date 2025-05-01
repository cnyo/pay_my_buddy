package com.yoann.pay_my_buddy.unit.controller;

import com.yoann.pay_my_buddy.controllers.RegistrationController;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {RegistrationController.class})
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    Errors errors;

    @Test
    public void getRegistrationPage_displaysForm() throws Exception {
        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(content().string(containsString("S'inscrire")))
                .andExpect(content().string(containsString("Mail")))
                .andExpect(content().string(containsString("Mot de passe")));
    }

    @Test
    public void postRegistration_andShowSuccessMessage() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setEmail("email@email.com");
        user.setPassword("password"); // todo: encoder

        when(errors.hasErrors()).thenReturn(false);
        when(userService.initUserFromRegistrationForm(any())).thenReturn(user);
        when(userService.addUser(any())).thenReturn(user);

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", user.getUsername())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login?registrationSuccess"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeExists("messageType"))
                .andExpect(flash().attribute("message", "Registration success !"))
                .andExpect(flash().attribute("messageType", "success"))
        ;
    }

    @Test
    public void postRegistration_withNotValidEmail_andShowErrorMessage() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "username")
                        .param("email", "emailemail.com")
                        .param("password", "password")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration?error"))
        ;
    }
}
