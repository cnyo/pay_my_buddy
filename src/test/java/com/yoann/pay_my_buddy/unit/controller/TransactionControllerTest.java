package com.yoann.pay_my_buddy.unit.controller;

import com.yoann.pay_my_buddy.controllers.TransactionController;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.TransactionService;
import com.yoann.pay_my_buddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.View;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc()
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransactionService transactionService;

    private User authUserMock;
    private User receiverUserMock;
    private Transaction transactionMock;
    @Autowired
    private View error;

    @BeforeEach
    public void setUp() {
        User authUser = new User();
        authUser.setId(1L);
        authUser.setUsername("jtest");
        authUser.setEmail("jtest@email");
        authUser.setPassword("password");

        User receiverUser = new User();
        receiverUser.setId(1L);
        receiverUser.setUsername("jtest");
        receiverUser.setEmail("jtest@email");
        receiverUser.setPassword("password");

        Transaction transaction = new Transaction();
        transaction.setId(1411L);
        transaction.setSenderUser(authUser);
        transaction.setReceiverUser(receiverUser);
        transaction.setDescription("Test transaction");
        transaction.setAmount(2000.00);

        authUserMock = authUser;
        receiverUserMock = receiverUser;
        transactionMock = transaction;
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void getTransactionPage_displaysAssociatedUsersAndForm() throws Exception {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setId(1411L);
        transaction.setSenderUser(authUserMock);
        transaction.setReceiverUser(receiverUserMock);
        transaction.setDescription("Test transaction");
        transaction.setAmount(2000.58);

        when(userService.getConnectedUsersFromUser(any())).thenReturn(List.of(receiverUserMock));
        when(userService.getUserByEmail(anyString())).thenReturn(authUserMock);
        when(transactionService.getAllTransactionsByUser(any())).thenReturn(List.of(transaction));

        // Act
        ResultActions result = mockMvc.perform(get("/transaction"));

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"));
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postFormTransaction_thenOk() throws Exception {
        // Arrange
        when(userService.getUserByEmail(anyString())).thenReturn(authUserMock);
        when(transactionService.initTransactionForAuthUser(any(), any())).thenReturn(transactionMock);
        when(transactionService.addTransaction(any())).thenReturn(transactionMock);

        // Act & Assert
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
                .andExpect(flash().attribute("receiver_username", receiverUserMock.getUsername()))
                .andExpect(flash().attribute("message_type", "success"))
        ;
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postFormTransaction_whenAmountIsNegative_thenRedirectedSuccess() throws Exception {
        // Arrange
        when(userService.getConnectedUsersFromUser(any())).thenReturn(List.of(receiverUserMock));
        when(transactionService.initTransactionForAuthUser(any(), any())).thenReturn(transactionMock);
        when(transactionService.addTransaction(any())).thenReturn(transactionMock);
        when(userService.getUserByEmail(anyString())).thenReturn(authUserMock);

        // Act & Assert
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
    }

    @Test
    @WithMockUser(username = "jtest@email.com")
    public void postFormTransaction_whenReceiverUserIsEmpty_thenRedirectedSuccess() throws Exception {
        // Arrange
        when(userService.getConnectedUsersFromUser(any())).thenReturn(List.of(receiverUserMock));
        when(transactionService.initTransactionForAuthUser(any(), any())).thenReturn(transactionMock);
        when(transactionService.addTransaction(any())).thenReturn(transactionMock);
        when(userService.getUserByEmail(anyString())).thenReturn(authUserMock);

        // Act & Assert
        mockMvc.perform(
                        post("/transaction")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("receiverUserId", "")
                                .param("description", "Test transaction")
                                .param("amount", "2000")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("transaction"))
                .andExpect(model().attributeHasFieldErrors("form", "receiverUserId"))
                .andExpect(model().attribute("message_type", "error"))
        ;
    }
}
