package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.exception.*;
import com.yoann.pay_my_buddy.forms.RegistrationForm;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import com.yoann.pay_my_buddy.service.ConnectionUserFactory;
import com.yoann.pay_my_buddy.service.UserService;
import com.yoann.pay_my_buddy.service.UserServiceImpl;
import com.yoann.pay_my_buddy.utils.EncoderUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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

    @Mock
    private EncoderUtils encoder;

    @BeforeAll
    public static void setUp() {
        userService = new UserServiceImpl();
    }

    @Test
    public void whenGetUserByEmail_thenReturnUser() throws UserNotFoundException {
        User user = new User();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(anyString());

        assertThat(result).isEqualTo(user);
    }

    @Test
    public void whenGetNotExistsUserByEmail_thenReturnException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(anyString())).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void addConnectionUser_thenSuccess() throws ConnectionUserException {
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
    public void addConnectionUser_whenUserToConnectIsNull_thenThrowException() throws NullUserConnectionUserException {
        assertThatThrownBy(() -> userService.addConnectionToUser(new User(), null)).isInstanceOf(NullUserConnectionUserException.class);
    }

    @Test
    public void tryToAddConnection_whenCurrentUserIsNull_thenThrowException() {
        assertThatThrownBy(() -> userService.addConnectionToUser(null, new User())).isInstanceOf(NullUserConnectionUserException.class);
    }

    @Test
    public void tryToAddConnection_withSameUser_thenThrowException() {
        User authUser = new User();
        authUser.setId(1L);

        assertThatThrownBy(() -> userService.addConnectionToUser(authUser, authUser)).isInstanceOf(SameUserConnectionUserException.class);
    }

    @Test
    public void tryToAddConnection_withUserWithoutId_thenThrowException() {
        assertThatThrownBy(() -> userService.addConnectionToUser(new User(), new User())).isInstanceOf(NullUserIdConnectionUserException.class);
    }

    @Test
    public void initUserFromRegistrationForm_withValidData_thenReturnUser() throws BadRegistrationDataException {
        BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

        RegistrationForm form = new RegistrationForm();
        form.setEmail("email@email.com");
        form.setPassword("password");
        String encodePassword = bCryptEncoder.encode(form.getPassword());

        User user = new User();
        user.setEmail("email@email.com");
        user.setPassword(encodePassword);

        when(encoder.encodePassword(anyString())).thenReturn(encodePassword);

        User result = userService.initUserFromRegistrationForm(form);

        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getEmail()).isEqualTo(form.getEmail());
        assertThat(result.getPassword()).isEqualTo(encodePassword);
    }

    @Test
    public void initUserFromRegistrationForm_withBadEmail_thenThrowException() {
        RegistrationForm form = new RegistrationForm();
        form.setEmail("email.email.com");
        form.setPassword("password");

        assertThatThrownBy(() -> userService.initUserFromRegistrationForm(form))
                .isInstanceOf(BadRegistrationDataException.class)
                .hasMessageContaining("Email is invalid");
    }
}
