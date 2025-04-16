package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.NegativeAmountException;
import com.yoann.pay_my_buddy.exception.SameUserInTransactionException;
import com.yoann.pay_my_buddy.exception.UserTransactionException;
import com.yoann.pay_my_buddy.mapper.TransactionMapper;
import com.yoann.pay_my_buddy.model.Transaction;
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
            throw new SameUserInTransactionException();
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
}
