package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.NegativeAmountException;
import com.yoann.pay_my_buddy.exception.SameUserTransactionException;
import com.yoann.pay_my_buddy.exception.UserTransactionException;
import com.yoann.pay_my_buddy.forms.TransactionForm;
import com.yoann.pay_my_buddy.mapper.TransactionMapper;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.TransactionRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LogManager.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Override
    public Iterable<Transaction> getTransactions() {
        log.debug("Call getTransactions");
        return transactionRepository.findAll();
    }

    @Override
    public Transaction addTransaction(Transaction transaction) throws UserTransactionException, IllegalArgumentException {
        if (transaction == null) {
            log.error("Transaction is null");
            throw new UserTransactionException("Transaction is null");
        }

        if (transaction.getSenderUser() == null || transaction.getReceiverUser() == null) {
            log.error("senderUser or receiverUser is null");
            throw new UserTransactionException("senderUser or receiverUser is null");
        }

        if (transaction.getSenderUser().equals(transaction.getReceiverUser())) {
            log.error("senderUser and receiverUser are the same");
            throw new SameUserTransactionException();
        }

        if (transaction.getAmount() < 0) {
            log.error("amount can't be negative");
            throw new NegativeAmountException();
        }

        transaction.setDate(new Date());
        Transaction insertedTransaction = transactionRepository.save(transaction);
        log.debug("Adding transaction successfully");

        return insertedTransaction;
    }

    @Override
    public List<TransactionDto> mapTransactionsToDtoList(Iterable<Transaction> transactions) {
        log.debug("Call mapToDtoList");
        List<TransactionDto> dtoList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionDto dto = transactionMapper.toDto(transaction);
            dtoList.add(dto);
        }
        log.debug("Mapped {} transactions to dto", dtoList.size());

        return dtoList;
    }

    public Transaction initTransactionForAuthUser(TransactionForm form, User authUser) throws IllegalArgumentException {
        log.debug("Call initTransaction");
        Transaction transaction = new Transaction();
        transaction.setDescription(form.getDescription());
        transaction.setAmount(form.getAmountAsDouble());

        if (form.getReceiverUserId() != null) {
            transaction.setReceiverUser(new User().setId(form.getReceiverUserIdAsLong()));
        } else {
            log.error("Receiver user id is null");
            throw new IllegalArgumentException("Receiver user id is null");
        }

        return attachSenderUser(transaction, authUser);
    }

    private Transaction attachSenderUser(Transaction transaction, User authUser) {
        log.debug("Call attachSenderUser");
        transaction.setSenderUser(authUser);

        return transaction;
    }
}
