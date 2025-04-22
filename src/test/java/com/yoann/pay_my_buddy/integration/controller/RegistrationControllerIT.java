package com.yoann.pay_my_buddy.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void postNewRegistration_withValidData_andShowSuccessMessage() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "username")
                        .param("email", "email@email.com")
                        .param("password", "password")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/login"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeExists("messageType"))
                .andExpect(flash().attribute("message", "Registration success !"))
                .andExpect(flash().attribute("messageType", "success"))
        ;
    }

    @Test
    public void postNewRegistration_withNotValidEmail_andShowErrorMessage() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "username")
                        .param("email", "emailemail.com")
                        .param("password", "password")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeExists("messageType"))
                .andExpect(flash().attribute("messageType", "warning"))
        ;
    }

    @Test
    public void postNewRegistration_withUsernameUpperTo50_andShowErrorMessage() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxy")
                        .param("email", "email@email.com")
                        .param("password", "password")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attributeExists("messageType"))
                .andExpect(flash().attribute("messageType", "warning"))
        ;
    }
}
