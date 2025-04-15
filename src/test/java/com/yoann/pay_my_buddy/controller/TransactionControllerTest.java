package com.yoann.pay_my_buddy.controller;

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


import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getTransactionPage_displaysAssociatedUsersAndForm() throws Exception {
        mockMvc.perform(get("/transaction"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(content().string(containsString("janedoe")));
    }

    @Test
    @WithMockUser(username = "jdoe")
    public void givenTransaction_whenPostTransaction_thenOk() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("senderUser", "");
        data.put("receiverUser", "2");
        data.put("description", "Test transaction");
        data.put("amount", "8888");

        mockMvc.perform(
                    post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("receiverUser", "2")
                        .param("description", "Test transaction")
                        .param("amount", "8888")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(content().string(containsString("janedoe")))
                .andExpect(content().string(containsString(data.get("description"))))
                .andExpect(content().string(containsString(data.get("amount"))))
        ;
    }
}
