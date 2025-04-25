package com.yoann.pay_my_buddy.integration.repository;

import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.ConnectionUserId;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.repository.UserRepository;
import com.yoann.pay_my_buddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryIT {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void whenFindAll_thenReturnAllUsers() {
        Iterable<User> users = userRepository.findAll();

        assertThat(users).isNotEmpty();
        assertThat(users.iterator().next()).isInstanceOf(User.class);
    }

    @Test
    public void givenId_whenFindById_thenReturnUser() {
        Optional<User> user = userRepository.findById(1L);

        assertThat(user.isPresent()).isTrue();
    }

    @Test
    public void givenUsername_whenFindByUsername_thenReturnUser() {
        Optional<User> user = userRepository.findByUsername("jdoe");

        assertThat(user.isPresent()).isTrue();
    }

    @Test
    public void givenUsername_whenFindByUsername_thenReturnAnyUser() {
        Optional<User> user = userRepository.findByUsername("jtest");

        assertThat(user.isPresent()).isFalse();
    }

    @Test
    public void givenEmail_whenFindByEmail_thenReturnUser() {
        Optional<User> user = userRepository.findByEmail("jdoe@email.com");

        assertThat(user.isPresent()).isTrue();
    }

    @Test
    public void givenNoneExistingId_whenFindById_thenReturnNull() {
        Optional<User> user = userRepository.findById(1000L);

        assertThat(user).isEmpty();
    }

    @Test
    public void givenNewUser_whenSave_thenSuccess() {
        User user = new User();
        user
                .setUsername("test")
                .setEmail("test@test.com")
                .setPassword("password");

        User insertedUser = userRepository.save(user);

        assertThat(insertedUser.getUsername()).isEqualTo("test");
        assertThat(insertedUser.getId()).isNotNull();
    }

    @Test
    public void givenNewUser_whenUpdate_thenSuccess() {
        User newUser = new User();
        newUser
                .setUsername("test")
                .setEmail("test@test.com")
                .setPassword("password");
        em.persist(newUser);
        em.flush();
        em.refresh(newUser);
        newUser.setUsername("updatedTest");

        User updatedUser = userRepository.save(newUser);

        assertThat(updatedUser.getUsername()).isEqualTo("updatedTest");
    }

    @Test
    // Est-ce qu'il faut bien supprimer le user si il est relié par des transactions ?
    public void givenUser_whenRemove_thenSuccess() {
        // Get main user to add association
        User toRemoveUser = em.find(User.class, 1);

        userRepository.delete(toRemoveUser);

        User user = em.find(User.class, 1);

        assertThat(user).isNull();
    }

    @Test
    public void attachNewAssociatedUser_whenSave_thenSuccess() {
        // Get main user to add association
        User mainUser = em.find(User.class, 1);
//        User mainUser = userRepository.findById(1);
        Integer initialCountConnectionUsers = mainUser.getConnections().size();

        // Given new user to associate
        User newUser = new User();
        newUser
                .setUsername("associated_test")
                .setEmail("test@test.com")
                .setPassword("password");
        User insertedAssociedUser = userRepository.save(newUser);

        ConnectionUser connectionUser = new ConnectionUser(mainUser, insertedAssociedUser, new Date());
        mainUser.addConnectionUser(connectionUser);

        User updatedMainUser = userRepository.save(mainUser);

        Optional<ConnectionUser> optResult = mainUser.getConnections().stream().filter(
                cu -> cu.getUser().getId().equals(1L) && cu.getAssociatedUser().equals(insertedAssociedUser)
        ).findFirst();

        assertThat(initialCountConnectionUsers).isEqualTo(1);
        assertThat(updatedMainUser.getConnections().size()).isEqualTo(2);
        assertThat(optResult.isPresent()).isTrue();
        assertThat(optResult.get().getUser()).isEqualTo(updatedMainUser);
        assertThat(optResult.get().getAssociatedUser()).isEqualTo(insertedAssociedUser);
    }

    @Test
    public void givenNewConnectionUser_whenAlreadyExists_thenFail() {
        // Get main user to add association
        User mainUser = em.find(User.class, 1);
        User associedUser = em.find(User.class, 2);

        ConnectionUser firstConnectionUser = new ConnectionUser(associedUser, mainUser, new Date());
        mainUser.addConnectionUser(firstConnectionUser);
        userRepository.save(mainUser);

        ConnectionUser duplicateConnectionUser = new ConnectionUser(associedUser, mainUser, new Date());
        mainUser.addConnectionUser(duplicateConnectionUser);

        assertThrows(DuplicateKeyException.class, () -> userRepository.save(mainUser));
    }

    @Test
    public void givenAssociatedUser_whenRemove_thenSuccess() {
        User mainUser = em.find(User.class, 1);
        User associatedUser = em.find(User.class, 2);
        ConnectionUser connectionUser = em.find(ConnectionUser.class, new ConnectionUserId(mainUser.getId(), associatedUser.getId()));

        mainUser.removeConnectionUser(connectionUser);
        User updatedMainUser = userRepository.save(mainUser);

        assertThat(updatedMainUser.getConnections().size()).isEqualTo(0);
        assertThat(em.find(User.class, 2)).isNotNull();
        assertThat(em.find(User.class, 2).getId()).isEqualTo(2);
    }

    @Test
    public void whenCountConnectionUser_thenReturnSize() {
        User mainUser = em.find(User.class, 1);

        assertThat(mainUser.getConnections().size()).isEqualTo(1);
    }

    @Test
    public void givenUserWithTransaction_whenSave_thenSuccess() {
        User senderUser = em.find(User.class, 1);
        User receiverUser = em.find(User.class, 2);
        int initialCountSenderTransactions = senderUser.getSenderTransactions().size();

        Transaction newTransaction = new Transaction();
        newTransaction
                .setReceiverUser(receiverUser)
                .setAmount(12500.00)
                .setDescription("A description test.")
                .setDate(new Date());

        senderUser.addSenderTransactions(newTransaction);

        User updatedSenderUser = userRepository.save(senderUser);

        assertThat(initialCountSenderTransactions).isEqualTo(1);
        assertThat(updatedSenderUser.getSenderTransactions().size()).isEqualTo(2);
    }

}
