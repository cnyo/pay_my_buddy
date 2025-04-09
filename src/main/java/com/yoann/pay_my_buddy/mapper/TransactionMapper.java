package com.yoann.pay_my_buddy.mapper;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TransactionMapper {
    private final Logger log = LogManager.getLogger(TransactionMapper.class);

    /**
     * Converts a {@link TransactionDto} into a {@link Transaction} entity.
     *
     * @param dto           the DTO containing transaction details (amount, description, etc.)
     * @param receiverUser  the user who will receive the transaction
     * @return              a new {@link Transaction} instance with the provided data and the current date
     */
    public Transaction toEntity(TransactionDto dto, User senderUser, User receiverUser) {
        log.debug("Convert TransactionDto to Transaction: {}", dto);

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setSenderUser(senderUser);
        transaction.setReceiverUser(receiverUser);
        transaction.setDate(new Date());

        return transaction;
    }
}
