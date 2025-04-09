package com.yoann.pay_my_buddy.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoann.pay_my_buddy.controllers.TransactionRestController;
import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.SameUserInTransactionException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.TransactionService;
import com.yoann.pay_my_buddy.service.UserService;
import org.apache.logging.log4j.util.InternalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionRestController.class)
public class TransactionRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransactionService transactionService;

    private TransactionDto transactionDto;

    @BeforeEach
    public void setup() {
        TransactionDto dto = new TransactionDto();
        dto.setSenderUserId(1L);
        dto.setReceiverUserId(2L);
        dto.setAmount(50.0);
        dto.setDescription("Test transaction");

        transactionDto = dto;
    }
    @WithMockUser(username = "spring")
    @Test
    public void postTransaction_ShouldReturnNewTransaction() throws Exception {
        // Arrange
        User connectedUser = new User();
        connectedUser.setId(1L);
        User receiverUser = new User();
        receiverUser.setId(2L);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setDescription(transactionDto.getDescription());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setSenderUser(connectedUser);
        transaction.setReceiverUser(receiverUser);
        transaction.setDate(new Date());

        given(userService.getUser(anyLong())).willReturn(connectedUser);
        given(userService.getUser(anyLong())).willReturn(receiverUser);
        given(transactionService.addTransaction(any(), any(), any())).willReturn(transaction);

        // Act & Assert
        mvc.perform(post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(transaction.getDescription()))
                .andExpect(jsonPath("$.amount").value(transaction.getAmount()))
                .andExpect(jsonPath("$.senderUserId").value(transaction.getSenderUser().getId()))
                .andExpect(jsonPath("$.receiverUserId").value(transaction.getReceiverUser().getId()));
    }

    @WithMockUser(username = "spring")
    @Test
    public void givenSameSenderAndReceiverUser_postTransaction_ShouldReturnSameUserInTransactionException() throws Exception {
        // Arrange
        given(userService.getUser(anyLong())).willReturn(new User());
        given(userService.getUser(anyLong())).willReturn(new User());
        given(userService.getUser(anyLong())).willThrow(new SameUserInTransactionException());

        // Act & Assert
        mvc.perform(post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Sender and receiver user can't be same")));
    }

    @WithMockUser(username = "spring")
    @Test
    public void givenNotExistsUser_postTransaction_ShouldThrowUserNotFoundException() throws Exception {
        given(userService.getUser(anyLong())).willThrow(new UserNotFoundException());

        // Act & Assert
        mvc.perform(post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found")));
    }

    @WithMockUser(username = "spring")
    @Test
    public void postTransaction_ShouldThrowInternalException() throws Exception {
        given(userService.getUser(anyLong())).willThrow(InternalException.class);

        // Act & Assert
        mvc.perform(post("/transaction")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto))
                )
                .andExpect(status().isInternalServerError());
    }
}
