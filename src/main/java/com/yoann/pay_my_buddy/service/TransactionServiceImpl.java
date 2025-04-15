package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.SameUserInTransactionException;
import com.yoann.pay_my_buddy.mapper.TransactionMapper;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.TransactionRepository;

import org.apache.coyote.BadRequestException;
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
    public Transaction addTransaction(TransactionDto transactionDto,  User senderUser, User receiverUser) throws IllegalArgumentException {
        if (senderUser.equals(receiverUser)) {
            log.error("senderUser and receiverUser are the same");
            throw new SameUserInTransactionException();
        }

        Transaction transaction = transactionMapper.toEntity(transactionDto, senderUser, receiverUser);
        log.debug("Adding transaction: {}", transaction);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction add(Transaction transaction) throws BadRequestException {
        if (transaction == null) {
            log.error("Transaction is null");
            throw new BadRequestException("Transaction is null");
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
