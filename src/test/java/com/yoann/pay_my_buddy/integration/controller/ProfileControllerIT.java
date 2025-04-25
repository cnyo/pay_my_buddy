package com.yoann.pay_my_buddy.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "jdoe")
    public void whenGetProfilePage_whenShowUserForm() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/profile"))
                .andDo(print());

        // Assert
        result
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile"))
                .andExpect(content().string(containsString("jdoe")))
                .andExpect(content().string(containsString("jdoe@email.com")));
    }

    @Test
    @WithMockUser(username = "jdoe")
    public void givenValidUserData_whenUpdatingProfile_thenRedirectWithSuccessMessage() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(put("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", "jdoe")
                        .param("email", "johndoe@email.com")
                        .param("password", "password")
                )
                .andDo(print());

        // Assert
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("message_type"))
                .andExpect(flash().attribute("message_type", "success"))
                .andExpect(flash().attributeExists("message"))
        ;
    }

    @Test
    @WithMockUser(username = "jdoe")
    public void givenUnValidUserData_whenUpdatingProfile_thenRedirectWithErrorMessage() throws Exception {
        // Act
        ResultActions badUsernameResult = mockMvc.perform(put("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxy")
                        .param("email", "johndoe@email.com")
                        .param("password", "password")
                )
                .andDo(print());

        ResultActions badEmailResult = mockMvc.perform(put("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", "johndoe")
                        .param("email", "johndoeemail.com")
                        .param("password", "password")
                )
                .andDo(print());

        // Assert
        badUsernameResult.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("message_type"))
                .andExpect(flash().attribute("message_type", "error"))
                .andExpect(flash().attributeExists("message"));

        badEmailResult.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("message_type"))
                .andExpect(flash().attribute("message_type", "error"))
                .andExpect(flash().attributeExists("message"));
    }
}
