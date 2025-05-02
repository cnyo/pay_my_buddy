package com.yoann.pay_my_buddy.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

@SpringBootTest
@ActiveProfiles("test") // todo: utilité ?
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql")
public class RegistrationControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getNewRegistrationPage_andShowFormRegistration() throws Exception {
        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration"))
                .andExpect(content().string(containsString("for=\"username_registration\"")))
                .andExpect(content().string(containsString("action=\"/registration\"")));
    }

    @Test
    public void postNewRegistration_withValidData_andRedirectToLoginPage() throws Exception {
        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "username")
                        .param("email", "email@email.com")
                        .param("password", "password")
                        .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/login?registrationSuccess"))
                .andExpect(flash().attribute("message_type", "success"))
        ;
    }

    @Test
    public void postNewRegistration_withNotValidData_redirectToRegistrationPageWithError() throws Exception {
        // Act
        ResultActions resultWithBadEmail = mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "username")
                .param("email", "emailemail.com")
                .param("password", "password")
                .with(csrf())
        );

        ResultActions resultWithBaUsername = mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "a".repeat(251))
                .param("email", "email@mail.com")
                .param("password", "password")
                .with(csrf())
        );


        //Expect
        resultWithBadEmail
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration?error"));

        resultWithBaUsername
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/registration?error"));
        ;
    }
}
