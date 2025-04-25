package com.yoann.pay_my_buddy.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "jdoe")
    public void getTransactionPage_displaysAssociatedUsersAndForm() throws Exception {
        mockMvc.perform(get("/transaction"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("/transaction"))
                .andExpect(content().string(containsString("janedoe")));
    }

    @Test
    @WithMockUser(username = "jdoe")
    public void postFormTransaction_thenOk() throws Exception {
        String text = "receiverUser=2&description=Test transaction&amount=2000";

        mockMvc.perform(
                    post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(text)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"))
                .andExpect(status().isFound())
                .andExpect(flash().attributeExists("receiver_username"))
                .andExpect(flash().attribute("receiver_username", "janedoe"))
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attribute("message", "success"))
        ;
    }

    @Test
    @WithMockUser(username = "jdoe")
    public void postFormTransaction_whenAmountIsNegative_thenRedirectedSuccess() throws Exception {
        String text = "receiverUser=2&description=Test transaction&amount=-2000";

        mockMvc.perform(
                    post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(text)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"))
                .andExpect(status().isFound())
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("error_message"))
                .andExpect(flash().attribute("message", "error"))
        ;
    }

    @Test
    @WithMockUser(username = "jdoe")
    public void postFormTransaction_whenReceiverUserIsEmpty_thenRedirectedSuccess() throws Exception {
        String text = "receiverUser=&description=Test transaction&amount=2000";

        mockMvc.perform(
                        post("/transaction")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(text)
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transaction"))
                .andExpect(status().isFound())
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("error_message"))
                .andExpect(flash().attribute("message", "error"))
        ;
    }
}
