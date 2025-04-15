package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.mapper.TransactionMapper;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class TransactionMapperTest {
    private final TransactionMapper transactionMapper = new TransactionMapper();

    @Test
    public void dtoToTransaction_shouldMapToTransactionCorrectly() {
        TransactionDto dto = new TransactionDto();
        User senderUser = new User();
        User receiverUser = new User();

        Transaction result = transactionMapper.toEntity(dto, senderUser, receiverUser);

        assertThat(result).isInstanceOf(Transaction.class);
        assertThat(result.getSenderUser()).isEqualTo(senderUser);
        assertThat(result.getReceiverUser()).isEqualTo(receiverUser);
    }

    @Test
    public void transactionToDto_shouldMapToDtoCorrectly() {
        Transaction transaction = new Transaction();

        User senderUser = new User();
        senderUser.setUsername("sender");

        User receiverUser = new User();
        receiverUser.setUsername("receiverUser");

        transaction.setSenderUser(senderUser);
        transaction.setReceiverUser(receiverUser);
        transaction.setAmount(2000.00);
        transaction.setDescription("Description");
        transaction.setDate(new Date());

        TransactionDto result = transactionMapper.toDto(transaction);

        assertThat(result).isInstanceOf(TransactionDto.class);
        assertThat(result.getReceiverUsername()).isEqualTo("receiverUser");
        assertThat(result.getDescription()).isEqualTo("Description");
        assertThat(result.getAmount()).isEqualTo(2000.00);
    }
}
