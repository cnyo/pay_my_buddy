package com.yoann.pay_my_buddy.integration.repository;

import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.repository.ConnectionUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

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

    @Test
    public void whenFindAll_thenReturnListOfConnectionUsers() {
        Iterable<ConnectionUser> connections = connectionUserRepository.findAll();
        Optional<ConnectionUser> connection = StreamSupport.stream(connections.spliterator(), false).findFirst();

        assertThat(connections).isNotEmpty();
        assertThat(connections).hasSize(1);
        assertThat(connection.isPresent()).isTrue();
        assertThat(connection.get().getUser1().getUsername()).isEqualTo("jtest");
        assertThat(connection.get().getUser2().getUsername()).isEqualTo("dtest");
    }

    @Test
    public void whenCountUsersInRelation_thenReturn1() {
        // Act
        Integer result = connectionUserRepository.countRelationForUsersId(1L, 2L);

        // Assert
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void whenCountUsersInRelation_thenReturn0() {
        // Act
        Integer result = connectionUserRepository.countRelationForUsersId(1L, 3L);

        // Assert
        assertThat(result).isEqualTo(0);
    }
}
