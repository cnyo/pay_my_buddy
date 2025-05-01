package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.UserTransactionException;
import com.yoann.pay_my_buddy.forms.TransactionForm;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransactionService {

    Iterable<Transaction> getTransactions();

    Transaction addTransaction(Transaction transaction) throws UserTransactionException, BadRequestException;

    List<TransactionDto> mapTransactionsToDtoList(Iterable<Transaction> transactions);

    Transaction initTransactionForAuthUser(TransactionForm form, User authUser) throws IllegalArgumentException;
}
