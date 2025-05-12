package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.NegativeAmountException;
import com.yoann.pay_my_buddy.exception.SameUserTransactionException;
import com.yoann.pay_my_buddy.exception.UserIsNotInRelationException;
import com.yoann.pay_my_buddy.exception.UserTransactionException;
import com.yoann.pay_my_buddy.forms.TransactionForm;
import com.yoann.pay_my_buddy.mapper.TransactionMapper;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.ConnectionUserRepository;
import com.yoann.pay_my_buddy.repository.TransactionRepository;
import com.yoann.pay_my_buddy.service.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

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
    private ConnectionUserRepository connectionUserRepository;

    @Mock
    TransactionMapper transactionMapper;

    @Test
    public void getTransactions_thenReturnNotEmptyIterable() {
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        Iterable<Transaction> mockTransactions = List.of(transaction1, transaction2);

        when(transactionRepository.findAll()).thenReturn(mockTransactions);

        Iterable<Transaction> transactions = transactionService.getTransactions();

        assertThat(transactions).isNotEmpty();
        assertThat(transactions).contains(transaction1, transaction2);
    }

    @Test
    public void getTransactions_thenReturnEmptyIterable() {
        Iterable<Transaction> mockTransactions = new ArrayList<>();

        when(transactionRepository.findAll()).thenReturn(mockTransactions);

        Iterable<Transaction> transactions = transactionService.getTransactions();

        assertThat(transactions).isEmpty();
    }

    @Test
    public void addTransaction_shouldSaveTransaction() throws UserTransactionException {
        User senderUser = new User();
        User receiverUser = new User();

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(2000.00);
        transaction.setSenderUser(senderUser);
        transaction.setReceiverUser(receiverUser);

        when(transactionRepository.save(any())).thenReturn(transaction);

        Transaction result = transactionService.addTransaction(transaction);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertThat(result).isInstanceOf(Transaction.class);
        assertThat(result.getAmount()).isEqualTo(2000.0);
    }

    @Test
    public void addTransaction_whenTransactionIsNull_shouldReturnError() {
        assertThatThrownBy(()-> transactionService.addTransaction(null)).isInstanceOf(UserTransactionException.class);
    }

    @Test
    public void addTransactionTransactionToHimself_throwsException() {
        User user = new User();

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(2000.00);
        transaction.setSenderUser(user);
        transaction.setReceiverUser(user);

        assertThatThrownBy(() -> transactionService.addTransaction(transaction)).isInstanceOf(SameUserTransactionException.class);
    }

    @Test
    public void addTransactionTransaction_whenAmountIsNegative_throwsException() {
        User senderUser = new User();
        User receiverUser = new User();

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(-2000.00);
        transaction.setSenderUser(senderUser);
        transaction.setReceiverUser(receiverUser);

        when(transactionRepository.save(any())).thenReturn(transaction);

        assertThatThrownBy(() -> transactionService.addTransaction(transaction)).isInstanceOf(NegativeAmountException.class);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    public void addTransactionTransaction_whenEmptyReceiver_throwsException() {
        User senderUser = new User();

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(2000.00);
        transaction.setSenderUser(senderUser);

        assertThatThrownBy(() -> transactionService.addTransaction(transaction)).isInstanceOf(UserTransactionException.class);
        verify(transactionRepository, times(0)).save(any(Transaction.class));
    }

    @Test
    public void mapTransactions_returnListOfTransactionDto() {
        // Arrange
        User senderUser = new User();
        senderUser.setUsername("sender");

        User receiverUser = new User();
        receiverUser.setUsername("receiverUser");

        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAmount(2000.00);
        transaction1.setDescription("Description");
        transaction1.setSenderUser(senderUser);
        transaction1.setReceiverUser(receiverUser);

        Iterable<Transaction> transactions = List.of(transaction1);

        TransactionDto dto = new TransactionDto(transaction1, senderUser);

        when(transactionMapper.toDto(any(), any())).thenReturn(dto);

        // Act
        List<TransactionDto> result = transactionService.mapTransactionsToDtoList(transactions, senderUser);

        // Assert
        verify(transactionMapper, times(1)).toDto(any(Transaction.class), any(User.class));
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getAmount()).isEqualTo(2000.0);
        assertThat(result.getFirst().getRelationUsername()).isEqualTo("receiverUser");
        assertThat(result.getFirst().getDescription()).isEqualTo("Description");
    }

    @Test
    public void givenTransactionFormAndAuthUser_whenInitTransaction_shouldReturnTransaction() throws UserIsNotInRelationException {
        // Arrange
        TransactionForm transactionForm = new TransactionForm();
        transactionForm.setDescription("Test");
        transactionForm.setAmount("2000");
        transactionForm.setReceiverUserId("2");

        User authUser = new User();
        authUser.setId(1L);
        authUser.setUsername("authUser");

        when(connectionUserRepository.countRelationForUsersId(any(), any())).thenReturn(1);

        // Act
        Transaction result = transactionService.initTransactionForAuthUser(transactionForm, authUser);

        // Assert
        assertThat(result).isInstanceOf(Transaction.class);
        assertThat(result.getReceiverUser().getId()).isEqualTo(2L);
        assertThat(result.getDescription()).isEqualTo("Test");
        assertThat(result.getAmount()).isEqualTo(2000.0);
        assertThat(result.getSenderUser()).isEqualTo(authUser);
    }

    @Test
    public void givenTransaction_whenReceiverIsNotARelation_shouldReturnException() {
        // Arrange
        User authUser = new User();

        TransactionForm transactionForm = new TransactionForm();
        transactionForm.setDescription("Test");
        transactionForm.setAmount("2000");
        transactionForm.setReceiverUserId("2");

        when(connectionUserRepository.countRelationForUsersId(any(), any())).thenReturn(0);

        // Act && Assert
        assertThatThrownBy(() -> transactionService.initTransactionForAuthUser(transactionForm, authUser)).isInstanceOf(UserIsNotInRelationException.class);
    }

    @Test
    public void givenTransactionFormAndAuthUser_whenInitTransactionWithEmptyReceiver_shouldReturnTransaction() {
        // Arrange
        TransactionForm transactionForm = new TransactionForm();
        transactionForm.setDescription("Test");
        transactionForm.setAmount("2000");

        User authUser = new User();
        authUser.setId(1L);
        authUser.setUsername("authUser");

        // Act && Assert
        assertThatThrownBy(() -> transactionService.initTransactionForAuthUser(transactionForm, authUser)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenTransactionUser_whenGetAllTransactions_shouldReturnTransactions() {
        // Arrange
        User authUser = new User();
        authUser.setId(1L);
        authUser.setUsername("authUser");

        User user = new User();
        authUser.setId(2L);
        authUser.setUsername("user");

        Transaction transaction = new Transaction();
        transaction.setId(1411L);
        transaction.setSenderUser(authUser);
        transaction.setReceiverUser(user);
        transaction.setDescription("Test transaction");
        transaction.setAmount(2000.58);

        when(transactionRepository.findAllByUser(authUser)).thenReturn(List.of(transaction));

        // Act
        List<Transaction> transactions = transactionService.getAllTransactionsByUser(authUser);

        // Assert
        assertThat(transactions.size()).isEqualTo(1);
        assertThat(transactions.getFirst().getAmount()).isEqualTo(2000.58);
    }

    @Test
    public void givenTransactionUser_whenGetAllTransactionsWithoutUser_shouldReturnException() {
        // Act && Assert
        assertThatThrownBy(() -> transactionService.getAllTransactionsByUser(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenTransactionUser_whenGetAllTransactionsWithUserWithoutId_shouldReturnException() {
        // Act && Assert
        assertThatThrownBy(() -> transactionService.getAllTransactionsByUser(new User())).isInstanceOf(NullPointerException.class);
    }
}
