package com.yoann.pay_my_buddy.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql")
public class ConnectionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void getAddConnectionPage_displaysForm() throws Exception {
        mockMvc.perform(get("/relation"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(content().string(containsString("email")));
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postForm_mustBeSuccess() throws Exception {
        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "wtest@email.com")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(flash().attribute("message", "connection added successfully"))
        ;
    }

    @Test
    @WithMockUser(username = "jdoe@email.com")
    public void postForm_withBadAuthUser_mustBeError() throws Exception {
        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "luc@email.com")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(flash().attribute("message", "User not found"))
        ;
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postForm_whenUserToConnectIsNotExists_mustContentErrorMessage() throws Exception {
        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "luce@email.com")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(flash().attribute("message", "User not found"));
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postForm_whenEmailIsBad_mustContentErrorMessage() throws Exception {
        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "lucemail.com")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(flash().attribute("message_type", "warning"));
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void whenPostConnection_whenUserAlreadyConnected_thenRedirectWithErrorMessage() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(post("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "dtest@email.com")
                )
                .andDo(print());

        // Assert
        result
                .andExpect(redirectedUrl("/relation"))
                .andExpect(status().isFound())
                .andExpect(flash().attribute("message", "User already connected"));
    }
}
