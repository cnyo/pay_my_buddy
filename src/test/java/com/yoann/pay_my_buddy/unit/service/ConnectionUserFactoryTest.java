package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.exception.NullUserConnectionUserException;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.ConnectionUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
public class ConnectionUserFactoryTest {

    private ConnectionUserFactory connectionUserFactory;

    @BeforeEach
    public void setup() {
        connectionUserFactory = new ConnectionUserFactory();
    }

    @Test
    public void createConnectionUser_forUsers_thenReturnConnectionUser() throws NullUserConnectionUserException {
        User currentUser = new User();
        currentUser.setId(1L);
        User userToConnect = new User();
        userToConnect.setId(2L);

        ConnectionUser result = connectionUserFactory.createConnectionUser(currentUser, userToConnect);

        assertThat(result).isInstanceOf(ConnectionUser.class);
        assertThat(result.getUser().getId()).isEqualTo(1L);
        assertThat(result.getAssociatedUser().getId()).isEqualTo(2L);
        assertThat(result.getDate()).isNotNull().isInstanceOf(Date.class);
    }

    @Test
    public void createConnectionUser_withNullUser_thenReturnException() {
        assertThatThrownBy(() -> connectionUserFactory.createConnectionUser(null, new User())).isInstanceOf(NullUserConnectionUserException.class);
    }

    @Test
    public void createConnectionUser_withNullUserToConnect_thenReturnException() {
        assertThatThrownBy(() -> connectionUserFactory.createConnectionUser(new User(), null)).isInstanceOf(NullUserConnectionUserException.class);
    }
}
