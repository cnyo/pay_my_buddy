package com.yoann.pay_my_buddy.unit.controller;

import com.yoann.pay_my_buddy.controllers.ProfileController;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.forms.ProfileForm;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<ProfileForm> formCaptor;

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void whenGetProfilePage_whenShowUserForm() throws Exception {
        // Arrange
        User authUser = new User();
        authUser.setUsername("jtest");
        authUser.setEmail("jtest@email.com");
        authUser.setPassword("password");

        when(userService.getUserByEmail(anyString())).thenReturn(authUser);

        // Act
        ResultActions result = mockMvc.perform(get("/profile"))
                .andDo(print());

        // Assert
        result
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("profile"))
                .andExpect(content().string(containsString("jtest")))
                .andExpect(content().string(containsString("jtest@email.com")));
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void whenGetProfilePage_whenUserNotFound_shouldThrowException() throws Exception {
        // Arrange
        when(userService.getUserByEmail(anyString())).thenThrow(UserNotFoundException.class);

        // Act
        ResultActions result = mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", "jtest")
                        .param("email", "test@email.com")
                        .param("password", "password")
                );

        // Assert
        result
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message_type", "warning"))
                .andExpect(flash().attribute("message", "User not found"));
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void givenValidUserData_whenUpdatingProfile_thenLogout() throws Exception {
        // Arrange
        User authUser = new User();
        authUser.setId(1L);
        authUser.setUsername("jdoe");
        authUser.setEmail("jdoe@email.com");
        authUser.setPassword("password");

        ProfileForm form = new ProfileForm();
        form.setUsername("jdoe");
        form.setEmail("jdoe@email.com");
        form.setPassword("password");

        User updatedUser = new User();
        authUser.setId(1L);
        updatedUser.setUsername(form.getUsername());
        updatedUser.setEmail(form.getEmail());
        updatedUser.setPassword(form.getPassword());

        when(userService.getUserByEmail(anyString())).thenReturn(authUser);
        when(userService.profileFormToUser(any(), any())).thenReturn(updatedUser);
        when(userService.updateUser(any())).thenReturn(updatedUser);

        // Act
        ResultActions result = mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", form.getUsername())
                        .param("email", form.getEmail())
                        .param("password", form.getPassword())
                )
                .andDo(print());

        // Assert
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trigger-logout"));

        verify(userService).profileFormToUser(any(), formCaptor.capture());
        ProfileForm capturedForm = formCaptor.getValue();
        assertThat(capturedForm.getUsername()).isEqualTo(updatedUser.getUsername());
        assertThat(capturedForm.getEmail()).isEqualTo(updatedUser.getEmail());
        assertThat(capturedForm.getPassword()).isNotBlank();
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void givenEmptyPassword_whenUpdatingProfile_thenRedirectWithErrorMessage() throws Exception {
        // Act
        ResultActions emptyPasswordResult = mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()
                        )
                        .param("username", "johndoe")
                        .param("email", "johndoe@email.com")
                        .param("password", "")
                )
                .andDo(print());

        // Assert
        emptyPasswordResult.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trigger-logout"));

        verify(userService, times(1)).profileFormToUser(any(), any());
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void givenUnValidUserData_whenUpdatingProfile_thenRedirectWithErrorMessage() throws Exception {
        // Act
        ResultActions badUsernameResult = mockMvc.perform(post("/profile")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .with(csrf()
                        )
                        .param("username", "a".repeat(51))
                        .param("email", "johndoe@email.com")
                        .param("password", "password")
                )
                .andDo(print());

        ResultActions badEmailResult = mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()
                        )
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

        verify(userService, times(0)).profileFormToUser(any(), any());
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void givenNoneExistsUserId_whenUpdatingProfile_thenRedirectWithErrorMessage() throws Exception {
        // Arrange
        when(userService.getUserByEmail(anyString())).thenThrow(NullPointerException.class);

        // Act
        ResultActions result = mockMvc.perform(post("/profile")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
                        .param("username", "jdoes")
                        .param("email", "johndoe@email.com")
                        .param("password", "password")
                )
                .andDo(print());

        // Assert
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("message_type", "warning"))
                .andExpect(flash().attribute("message", "User not found"));

        verify(userService, times(0)).profileFormToUser(any(), any());
    }

}
