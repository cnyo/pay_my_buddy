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
@ActiveProfiles("test") // todo: utilité ?
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql")
public class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void getTransactionPageForJtest_displaysAssociatedUsersAndForm() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/transaction"));

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(content().string(containsString("dtest")));
    }

    @Test
    @WithMockUser(username = "wtest@email.com")
    public void getTransactionPageForWtest_displaysAssociatedUsersAndForm() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(get("/transaction"));

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(content().string(containsString("dtest")));
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postFormTransaction_thenOk() throws Exception {
        mockMvc.perform(
                    post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("receiverUserId", "2")
                        .param("description", "Test transaction")
                        .param("amount", "2000")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"))
                .andExpect(status().isFound())
                .andExpect(flash().attribute("message_type", "success"))
        ;
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postFormTransaction_whenAmountIsNegative_thenRedirectedSuccess() throws Exception {
        mockMvc.perform(
                    post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("receiverUserId", "2")
                        .param("description", "Test transaction")
                        .param("amount", "-2000")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeHasFieldErrors("form", "amount"))
                .andExpect(model().attribute("message_type", "error"));
        ;
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postFormTransaction_whenReceiverUserIsEmpty_thenRedirectedSuccess() throws Exception {
        mockMvc.perform(
                        post("/transaction")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("receiverUserId", "")
                                .param("description", "Test transaction")
                                .param("amount", "-2000")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeHasFieldErrors("form", "receiverUserId"))
                .andExpect(model().attribute("message_type", "error"))
        ;
    }
}
