package com.yoann.pay_my_buddy.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/data-test.sql")
public class AuthControllerIT {
     @Autowired
     private MockMvc mockMvc;

     @Test
     public void getLoginPage_displaysForm() throws Exception {
          // Act
          ResultActions result = mockMvc.perform(get("/login"));

          // Assert
          result.andDo(print())
                  .andExpect(status().isOk())
                  .andExpect(view().name("login"))
                  .andExpect(content().string(containsString("password")));
     }
}
