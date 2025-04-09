package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.SameUserInTransactionException;
import com.yoann.pay_my_buddy.mapper.TransactionMapper;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.TransactionRepository;
import com.yoann.pay_my_buddy.service.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    TransactionMapper transactionMapper;

    @Test
    public void addTransaction_shouldSaveTransaction() {
        TransactionDto dto = new TransactionDto();
        dto.setAmount(2000.0);

        User senderUser = new User();
        User receiverUser = new User();

        Transaction transaction = new Transaction();
        transaction.setAmount(2000.0);

        when(transactionMapper.toEntity(any(), any(), any())).thenReturn(transaction);
        when(transactionRepository.save(any())).thenReturn(transaction);

        Transaction result = transactionService.addTransaction(dto, senderUser, receiverUser);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertThat(result).isInstanceOf(Transaction.class);
        assertThat(result.getAmount()).isEqualTo(2000.0);
    }

    @Test
    public void givenSameReceiverAndSenderUser_addTransaction_shouldThrowSameUserInTransactionException() {
        TransactionDto dto = new TransactionDto();
        dto.setAmount(2000.0);

        User user = new User();

        Transaction transaction = new Transaction();
        transaction.setAmount(2000.0);
        transaction.setSenderUser(user);
        transaction.setReceiverUser(user);

        when(transactionMapper.toEntity(any(), any(), any())).thenReturn(transaction);

        assertThatThrownBy(() -> transactionService.addTransaction(dto, user, user)).isInstanceOf(SameUserInTransactionException.class);
    }

    @Test
    public void addTransactionToYourself_throwsException() {
        TransactionDto dto = new TransactionDto();
        dto.setAmount(2000.0);
        User senderUser = new User();

        assertThatThrownBy(() -> transactionService.addTransaction(dto, senderUser, senderUser)).isInstanceOf(IllegalArgumentException.class);
    }
}
