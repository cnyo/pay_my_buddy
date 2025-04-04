package com.yoann.pay_my_buddy.integration.repository;

import com.yoann.pay_my_buddy.repository.UserRepository;
import com.yoann.pay_my_buddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
        Optional<User> user = userRepository.findById(1);

        assertThat(user.isPresent()).isTrue();
    }

    @Test
    public void givenId_whenFindByUsername_thenReturnUser() {
        Optional<User> user = userRepository.findByUsername("jtest");

        assertThat(user.isPresent()).isTrue();
    }

    @Test
    public void givenId_whenFindByEmail_thenReturnUser() {
        Optional<User> user = userRepository.findByEmail("jtest@email.com");

        assertThat(user.isPresent()).isTrue();
    }

    @Test
    public void givenNoneExistingId_whenFindById_thenReturnNull() {
        Optional<User> user = userRepository.findById(1000);

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
    public void givenAssociatedUser_whenSave_thenSuccess() {
        // Given new user to associate
        User newUser = new User();
        newUser
                .setUsername("associated_test")
                .setEmail("test@test.com")
                .setPassword("password");
        User insertedNewUser = userRepository.save(newUser);

        // Get main user to add associated user
        User mainUser = em.find(User.class, 1);
        mainUser.addUser(insertedNewUser);

        User updatedMainUser = userRepository.save(mainUser);
        Optional<User> optAssociatedUserResult = updatedMainUser.getAssociatedUsers().stream()
                .filter(u -> u.getUsername().equals("associated_test"))
                .findFirst();

        assertThat(updatedMainUser.getId()).isEqualTo(1);
        assertThat(updatedMainUser.getAssociatedUsers().size()).isEqualTo(2);
        assertThat(optAssociatedUserResult.isPresent()).isTrue();
        assertThat(optAssociatedUserResult.get().getUsername()).isEqualTo("associated_test");
    }

    @Test
    public void removeAssociatedUser_whenSave_thenSuccess() {
        User mainUser = em.find(User.class, 1);
        User associatedUser = em.find(User.class, 2);

        mainUser.remove(associatedUser);
        User updatedMainUser = userRepository.save(mainUser);

        assertThat(updatedMainUser.getAssociatedUsers().size()).isEqualTo(0);
        assertThat(em.find(User.class, 2)).isNotNull();
        assertThat(em.find(User.class, 2).getId()).isEqualTo(2);
    }

}
