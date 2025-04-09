package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.SameUserInTransactionException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.TransactionService;
import com.yoann.pay_my_buddy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    private final Logger log = LoggerFactory.getLogger(TransactionRestController.class);

    @PostMapping("/transaction")
    public ResponseEntity<?> newTransaction(@RequestBody TransactionDto transactionDto) {
        log.info("/transaction New transaction");

        try {
            User connectedUser = userService.getUser(1L);
            User receiverUser = userService.getUser(transactionDto.getSenderUserId());
            Transaction transaction = transactionService.addTransaction(transactionDto, connectedUser, receiverUser);
            log.info("Transaction created");

            return ResponseEntity.ok(new TransactionDto(transaction));
        } catch(UserNotFoundException e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(SameUserInTransactionException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch(Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }
}
