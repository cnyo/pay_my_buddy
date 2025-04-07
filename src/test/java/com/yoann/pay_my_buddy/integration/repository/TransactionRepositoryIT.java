package com.yoann.pay_my_buddy.integration.repository;

import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class TransactionRepositoryIT {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void whenFindAll_thenReturnAllTransactions() {
        Iterable<Transaction> transactions = transactionRepository.findAll();

        assertThat(transactions).isNotEmpty();
        assertThat(transactions.iterator().next()).isInstanceOf(Transaction.class);
    }

    @Test
    public void givenId_whenFindById_thenReturnTransaction() {
        Optional<Transaction> transaction = transactionRepository.findById(1);

        assertThat(transaction.isPresent()).isTrue();
        assertThat(transaction.get().getId()).isEqualTo(1);
        assertThat(transaction.get().getSenderUser()).isEqualTo(1);
        assertThat(transaction.get().getReceiverUser()).isEqualTo(2);
    }

    @Test
    public void givenNoneExistingId_whenFindById_thenReturnNull() {
        Optional<Transaction> transaction = transactionRepository.findById(1000);

        assertThat(transaction).isEmpty();
    }

    @Test
    public void givenNewTransaction_whenSave_thenSuccess() {
        User senderUser = em.find(User.class, 2);
        User receiverUser = em.find(User.class, 1);

        Transaction transaction = new Transaction();
        transaction
                .setSenderUser(senderUser)
                .setReceiverUser(receiverUser)
                .setAmount(12500.00)
                .setDescription("A description test.")
                .setDate(new Date());

        Transaction insertedTransaction = transactionRepository.save(transaction);

        assertThat(insertedTransaction.getSenderUser().getId()).isEqualTo(senderUser.getId());
        assertThat(insertedTransaction.getReceiverUser().getId()).isEqualTo(receiverUser.getId());
        assertThat(insertedTransaction.getAmount()).isEqualTo(12500.00);
        assertThat(insertedTransaction.getId()).isNotNull();
    }

    @Test
    public void givenTransaction_whenUpdate_thenSuccess() {
        User senderUser = em.find(User.class, 1);
        User receiverUser = em.find(User.class, 2);
        Transaction newTransaction = new Transaction();
        newTransaction
                .setSenderUser(senderUser)
                .setReceiverUser(receiverUser)
                .setAmount(12500.00)
                .setDescription("A description test.")
                .setDate(new Date());

        Transaction insertedTransaction = transactionRepository.save(newTransaction);
        insertedTransaction.setDescription("Another description updated.");
        Transaction updatedTransaction = transactionRepository.save(insertedTransaction);

        assertThat(updatedTransaction.getId()).isNotNull();
        assertThat(updatedTransaction.getDescription()).isEqualTo("Another description updated.");
        assertThat(updatedTransaction.getId()).isEqualTo(insertedTransaction.getId());
    }
}
