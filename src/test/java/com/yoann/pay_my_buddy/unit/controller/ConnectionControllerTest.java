package com.yoann.pay_my_buddy.unit.controller;

import com.yoann.pay_my_buddy.controllers.ConnectionController;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.ConnectionUserServiceImpl;
import com.yoann.pay_my_buddy.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ConnectionController.class})
@AutoConfigureMockMvc
public class ConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private ConnectionUserServiceImpl connectionUserService;

    @Test
    @WithMockUser(username = "jdoe")
    public void whenPostAddConnection_thenRedirectWithSuccessMessage() throws Exception {
        String email = "test@test.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("test");

        User currentUser = new User();
        currentUser.setUsername("authUser");

        ConnectionUser connectionUser = new ConnectionUser();
        connectionUser.setUser(user);
        connectionUser.setAssociatedUser(user);

        when(userService.getUserByEmail(anyString())).thenReturn(user);
        when(connectionUserService.newConnectionWithUser(any())).thenReturn(connectionUser);
        when(userService.attachConnectionToUser(any(), any())).thenReturn(currentUser);
        when(userService.updateUser(any())).thenReturn(user);

        mockMvc.perform(post("/relation")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", email)
            )
            .andDo(print())
            .andExpect(redirectedUrl("/relation"))
            .andExpect(status().isFound())
            .andExpect(flash().attributeCount(1))
            .andExpect(flash().attribute("success", "connection added successfully"))
            ;
    }

    @Test
    @WithMockUser(username = "jdoe")
    public void whenPostAddConnectionNotExistsUser_thenRedirectWithNotFoundException() throws Exception {
        String email = "test@test.com";

        when(userService.getUserByEmail(anyString())).thenThrow(UserNotFoundException.class);

        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                )
                .andDo(print())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(status().isFound())
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attribute("error", "User not found"))
        ;

        verify(userService, times(1)).getUserByEmail(anyString());
        verify(userService, times(0)).addConnectionToUser(any(), any());
    }

    @ParameterizedTest()
    @ValueSource(strings = {"", " ", "bad-email"})
    @WithMockUser(username = "jdoe")
    public void whenPostAddConnection_withBadEmail_thenRedirectedWithError(String email) throws Exception {
        mockMvc.perform(post("/relation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email)
                )
                .andDo(print())
                .andExpect(redirectedUrl("/relation"))
                .andExpect(status().isFound())
                .andExpect(flash().attributeCount(1))
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", anyOf(
                        equalTo("Errors in relation"),
                        equalTo("Email is invalid")
                )))
        ;
    }
}
