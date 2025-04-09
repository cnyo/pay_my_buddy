package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.mapper.TransactionMapper;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
}
