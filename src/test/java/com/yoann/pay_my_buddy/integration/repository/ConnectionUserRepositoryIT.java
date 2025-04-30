package com.yoann.pay_my_buddy.integration.repository;

import com.yoann.pay_my_buddy.PayMyBuddyApplication;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.ConnectionUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/data-test.sql")
public class ConnectionUserRepositoryIT {

    @Autowired
    private ConnectionUserRepository connectionUserRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    public void whenFindAll_thenReturnListOfConnectionUsers() {
        Iterable<ConnectionUser> connections = connectionUserRepository.findAll();
        Optional<ConnectionUser> connection = StreamSupport.stream(connections.spliterator(), false).findFirst();

        assertThat(connections).isNotEmpty();
        assertThat(connections).hasSize(1);
        assertThat(connection.isPresent()).isTrue();
        assertThat(connection.get().getUser().getUsername()).isEqualTo("jtest");
        assertThat(connection.get().getAssociatedUser().getUsername()).isEqualTo("dtest");
    }

    @Test
    public void whenFindByUser_thenReturnListOfConnectionUsers() {
        User user = em.find(User.class, 1);
        Iterable<ConnectionUser> connections = connectionUserRepository.findByUser(user);
        Optional<ConnectionUser> connection = StreamSupport.stream(connections.spliterator(), false).findFirst();

        assertThat(connections).isNotEmpty();
        assertThat(connections).hasSize(1);
        assertThat(connection.isPresent()).isTrue();
        assertThat(connection.get().getUser()).isEqualTo(user);
        assertThat(connection.get().getAssociatedUser()).isNotEqualTo(user);
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
