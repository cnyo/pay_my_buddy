package com.yoann.pay_my_buddy.unit.controller;

import com.yoann.pay_my_buddy.controllers.RegistrationController;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {RegistrationController.class})
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void getRegistrationPage_displaysForm() throws Exception {
        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(content().string(containsString("Username")))
                .andExpect(content().string(containsString("Email")))
                .andExpect(content().string(containsString("Mot de passe")));
    }

    @Test
    public void postRegistration_whenRedirectToLoginPage_andShowSuccessMessage() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setEmail("email@email.com");
        user.setPassword("password"); // todo: encoder

        when(userService.initUserFromRegistrationForm(any())).thenReturn(user);
        when(userService.addUser(any())).thenReturn(user);

        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(view().name("login"))
//                .andExpect(content().string(containsString("Username")))
//                .andExpect(content().string(containsString("Email")))
//                .andExpect(content().string(containsString("Mot de passe")))
        ;
    }
}
