package com.yoann.pay_my_buddy.integration.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql")
public class ProfileControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void whenGetProfilePageJtest_whenShowUserForm() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/profile"))
                .andDo(print());

        // Assert
        result
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile"))
                .andExpect(content().string(containsString("jtest")));
    }

    @Test
    @WithMockUser(username = "wtest@email.com")
    public void whenGetProfilePageWtest_whenShowUserForm() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/profile"))
                .andDo(print());

        // Assert
        result
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile"))
                .andExpect(content().string(not(containsString("jtest"))))
                .andExpect(content().string(containsString("wtest@email.com")));
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void givenValidUserData_whenUpdatingProfile_thenRedirectWithSuccessMessage() throws Exception {
        mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", "rtest")
                        .param("email", "rtest@email.com")
                        .param("password", "password")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trigger-logout"))
        ;
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void givenUnValidUserData_whenUpdatingProfile_thenRedirectWithErrorMessage() throws Exception {
        // Act
        ResultActions badUsernameResult = mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", "a".repeat(51))
                        .param("email", "johndoe@email.com")
                        .param("password", "password")
                )
                .andDo(print());

        ResultActions badEmailResult = mockMvc.perform(post("/profile")
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
                .andExpect(flash().attribute("message_type", "warning"))
                .andExpect(flash().attributeExists("message"));

        badEmailResult.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("message_type"))
                .andExpect(flash().attribute("message_type", "warning"))
                .andExpect(flash().attributeExists("message"));
    }

    @ParameterizedTest(name = "{index} => password={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    @WithMockUser(username = "jtest@email.com")
    public void givenUnValidUserData_whenUpdatingProfile_thenRedirectWithErrorMessage(String password) throws Exception {
        // Act
        ResultActions emptyPasswordResult = mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", "jtest")
                        .param("email", "jtest@email.com")
                        .param("password", password)
                )
                .andDo(print());

        // Assert
        emptyPasswordResult
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trigger-logout"));
    }
}
