package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {

    Transaction addTransaction(TransactionDto transactionDto, User senderUser, User receiverUser);
}
