package com.yoann.pay_my_buddy.integration.repository.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ConnectionControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAddConnectionPage_displaysForm() throws Exception {
        mockMvc.perform(get("/relation"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(content().string(containsString("email")));
    }

    @Test
    public void postForm_mustBeSuccess() throws Exception {
        mockMvc.perform(get("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "luc@email.com")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(content().string(containsString("email")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("flashAttribute"));
    }

    @Test
    public void postForm_whenEmailIsBad_mustContentErrorMessage() throws Exception {
        mockMvc.perform(get("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "lucemail.com")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("relation"))
                .andExpect(content().string(containsString("email")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("flashAttribute"));
    }
}
