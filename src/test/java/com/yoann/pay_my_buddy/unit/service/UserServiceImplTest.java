package com.yoann.pay_my_buddy.unit.service;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.yoann.pay_my_buddy.exception.SameUserInConnectionUserException;
import com.yoann.pay_my_buddy.exception.UserIsNullException;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import com.yoann.pay_my_buddy.service.ConnectionUserFactory;
import com.yoann.pay_my_buddy.service.UserService;
import com.yoann.pay_my_buddy.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private static UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConnectionUserFactory connectionUserFactory;

    @BeforeAll
    public static void setUp() {
        userService = new UserServiceImpl();
    }

    @Test
    public void addConnectionUser_thenSuccess() throws InvalidTypeIdException {
        User authUser = new User();
        authUser.setId(1L);
        User userToConnect = new User();
        userToConnect.setId(2L);

        ConnectionUser connectionUser = new ConnectionUser();
        connectionUser.setUser(authUser);
        connectionUser.setAssociatedUser(userToConnect);
        authUser.addConnectionUser(connectionUser);

        when(connectionUserFactory.createConnectionUser(authUser, userToConnect)).thenReturn(connectionUser);
        when(userRepository.save(any())).thenReturn(authUser);

        User result = userService.addConnectionToUser(authUser, userToConnect);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getConnections().size()).isEqualTo(1);
        assertThat(result.getConnections()).contains(connectionUser);

        verify(connectionUserFactory, times(1)).createConnectionUser(authUser, userToConnect);
        verify(userRepository, times(1)).save(authUser);
    }

    @Test
    public void tryToAddConnection_whenUserToConnectIsNull_thenThrowException() {
        assertThatThrownBy(() -> userService.addConnectionToUser(new User(), null)).isInstanceOf(UserIsNullException.class);
    }

    @Test
    public void tryToAddConnection_whenCurrentUserIsNull_thenThrowException() {
        assertThatThrownBy(() -> userService.addConnectionToUser(null, new User())).isInstanceOf(UserIsNullException.class);
    }

    @Test
    public void tryToAddConnection_withSameUser_thenThrowException() {
        User authUser = new User();
        authUser.setId(1L);

        assertThatThrownBy(() -> userService.addConnectionToUser(authUser, authUser)).isInstanceOf(SameUserInConnectionUserException.class);
    }

    @Test
    public void tryToAddConnection_withUserWithoutId_thenThrowException() {
        assertThatThrownBy(() -> userService.addConnectionToUser(new User(), new User())).isInstanceOf(IllegalArgumentException.class);
    }
}
