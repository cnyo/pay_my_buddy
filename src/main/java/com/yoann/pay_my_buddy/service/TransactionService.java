package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.UserIsNotInRelationException;
import com.yoann.pay_my_buddy.exception.UserTransactionException;
import com.yoann.pay_my_buddy.forms.TransactionForm;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for transaction-related business logic.
 */
@Service
public interface TransactionService {

    /**
     * Retrieves all transactions.
     *
     * @return an {@link Iterable} collection of {@link Transaction} entities.
     */
    Iterable<Transaction> getTransactions();

    /**
     * Adds a new transaction after validating its content.
     *
     * @param transaction the {@link Transaction} to add.
     * @return the persisted {@link Transaction} entity.
     * @throws UserTransactionException if the transaction is invalid (null, same sender/receiver, etc.).
     * @throws IllegalArgumentException if an argument is not valid.
     */
    Transaction addTransaction(Transaction transaction) throws UserTransactionException, BadRequestException;

    /**
     * Maps a collection of {@link Transaction} entities to a list of {@link TransactionDto} objects,
     * using the authenticated user to contextualize each transaction.
     *
     * @param transactions an {@link Iterable} of transactions to convert; must not be {@code null}
     * @param authUser the currently authenticated user, used to personalize each DTO
     * @return a {@link List} of {@link TransactionDto} instances corresponding to the input transactions
     * @throws IllegalArgumentException if {@code transactions} or {@code authUser} is {@code null}
     */
    List<TransactionDto> mapTransactionsToDtoList(Iterable<Transaction> transactions, User authUser);

    /**
     * Initializes a transaction from the authenticated user to the recipient specified in the form.
     *
     * @param form the form containing transaction details, including the recipient's email and the amount
     * @param authUser the currently authenticated user initiating the transaction
     * @return the created {@link Transaction} entity with all fields populated
     * @throws IllegalArgumentException if the transaction amount is invalid or the recipient email is missing
     * @throws UserIsNotInRelationException if the recipient is not connected to the authenticated user
     */
    Transaction initTransactionForAuthUser(TransactionForm form, User authUser) throws IllegalArgumentException, UserIsNotInRelationException;

    /**
     * Sets the sender user of a {@link Transaction}.
     *
     * @param transaction the transaction to update.
     * @param authUser    the authenticated user to set as sender.
     * @return the updated {@link Transaction}.
     */
    Transaction attachSenderUser(Transaction transaction, User authUser);

    /**
     * Retrieves all transactions associated with a specific user.
     *
     * @param user the {@link User} whose transactions are to be retrieved.
     * @return a {@link List} of {@link Transaction} entities associated with the specified user.
     */
    List<Transaction> getAllTransactionsByUser(User user) throws NullPointerException;
}
