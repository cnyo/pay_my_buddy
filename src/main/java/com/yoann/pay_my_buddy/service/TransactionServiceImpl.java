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

/**
 * Service implementation for handling transactions.
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LogManager.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    /**
     * Retrieves all transactions from the database.
     *
     * @return an {@link Iterable} of all {@link Transaction} entities.
     */
    @Override
    public Iterable<Transaction> getTransactions() {
        log.debug("Call getTransactions");
        return transactionRepository.findAll();
    }

    /**
     * Adds a new transaction after validating the provided data.
     *
     * @param transaction the {@link Transaction} to be added.
     * @return the saved {@link Transaction} entity.
     * @throws UserTransactionException if the transaction or its users are invalid.
     * @throws IllegalArgumentException if arguments are not valid.
     */
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

    /**
     * Converts a list of {@link Transaction} entities to a list of {@link TransactionDto}.
     *
     * @param transactions the transactions to map.
     * @return a list of mapped {@link TransactionDto}.
     */
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

    /**
     * Initializes a {@link Transaction} from a form and the authenticated user.
     *
     * @param form     the submitted {@link TransactionForm}.
     * @param authUser the authenticated {@link User} who initiates the transaction.
     * @return a partially built {@link Transaction} entity.
     * @throws IllegalArgumentException if the receiver user ID is null.
     */
    @Override
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

    /**
     * Sets the sender user of a {@link Transaction}.
     *
     * @param transaction the transaction to update.
     * @param authUser    the authenticated user to set as sender.
     * @return the updated {@link Transaction}.
     */
    @Override
    public Transaction attachSenderUser(Transaction transaction, User authUser) {
        log.debug("Call attachSenderUser");
        transaction.setSenderUser(authUser);

        return transaction;
    }
}
