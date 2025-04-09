package com.yoann.pay_my_buddy.integration.repository;

import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.ConnectionUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class ConnectionUserRepositoryIT {

    @Autowired
    private ConnectionUserRepository connectionUserRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void whenFindAll_thenReturnListOfConnectionUsers() {
        Iterable<ConnectionUser> connections = connectionUserRepository.findAll();

        assertThat(connections).isNotEmpty();
    }

    @Test
    public void whenFindByUser_thenReturnListOfConnectionUsers() {
        User user = em.find(User.class, 1);
        Iterable<ConnectionUser> connections = connectionUserRepository.findByUser(user);

        assertThat(connections).isNotEmpty();
        assertThat(StreamSupport.stream(connections.spliterator(), false).count()).isEqualTo(1);
        assertThat(StreamSupport.stream(connections.spliterator(), false).findFirst().get().getUser()).isEqualTo(user);
        assertThat(StreamSupport.stream(connections.spliterator(), false).findFirst().get().getAssociatedUser()).isNotEqualTo(user);
    }

    @Test
    public void whenFindByAssociatedUser_thenReturnListOfConnectionUsers() {
        User user = em.find(User.class, 2);
        Iterable<ConnectionUser> connections = connectionUserRepository.findByAssociatedUser(user);

        assertThat(connections).isNotEmpty();
        assertThat(StreamSupport.stream(connections.spliterator(), false).findFirst().get().getAssociatedUser()).isEqualTo(user);
        assertThat(StreamSupport.stream(connections.spliterator(), false).findFirst().get().getUser()).isNotEqualTo(user);
    }
}
