package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.enums.UserExceptionMessage;
import com.yoann.pay_my_buddy.exception.*;
import com.yoann.pay_my_buddy.forms.ProfileForm;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
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
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("jtest");
        user1.setEmail("jtest@email.com");
        user1.setPassword("password");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("wtest");
        user2.setEmail("wtest@email.com");
        user2.setPassword("password");

        ConnectionUser connectionUser = new ConnectionUser(user1, user2, new Date());

        when(connectionUserFactory.createConnectionUser(any(), any())).thenReturn(connectionUser);
        when(userRepository.save(any())).thenReturn(user1);

        // Act
        User result = userService.addConnectionToUser(user1, user2);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getUser1Connections().size()).isEqualTo(1);
        assertThat(result.getUser1Connections()).contains(connectionUser);
        assertThat(result.getUser1Connections().stream().findFirst().isPresent()).isTrue();
        assertThat(result.getUser1Connections().stream().findFirst().get().getUser1()).isEqualTo(user1);
        assertThat(result.getUser1Connections().stream().findFirst().get().getUser2()).isEqualTo(user2);

        verify(connectionUserFactory, times(1)).createConnectionUser(user1, user2);
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    public void addConnectionUser_whenUserToConnectIsNull_thenThrowException() {
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

    @Test
    public void givenUsername_whenGetUser_thenReturnUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@email.com");
        user.setPassword("password");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserByUsername("jdoe");

        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void givenEmptyUsername_whenGetUser_thenReturnException() {
        // Assert
        assertThatThrownBy(() -> userService.getUserByUsername(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenUsernameNotExists_whenGetUser_thenReturnException() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> userService.getUserByUsername("username")).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void givenProfileForm_whenConvertProfileFormToUser_thenReturnUser() {
        // Arrange
        ProfileForm form = new ProfileForm();
        form.setUsername("jdoes");
        form.setEmail("email@email.com");
        form.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@email.com");
        user.setPassword("password");

        when(userRepository.save(any())).thenReturn(user);

        // Act
        User result = userService.profileFormToUser(user, form);

        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo(form.getUsername());
    }

    @Test
    public void givenNullProfileForm_whenConvertProfileFormToUser_thenThrowException() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@email.com");
        user.setPassword("password");

        // Assert
        assertThatThrownBy(() -> userService.profileFormToUser(user, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining(UserExceptionMessage.USER_PROFILE_FORM_IS_NULL.getMessage());
    }

    @ParameterizedTest(name = "{index} => password={0}")
    @NullSource
    @ValueSource(strings = {"", " "})
    public void givenProfileFormWithoutPassword_whenConvertProfileFormToUser_thenReturnUser(String password) {
        // Arrange
        ProfileForm form = new ProfileForm();
        form.setUsername("jdoes");
        form.setEmail("email@email.com");
        form.setPassword(password);

        User user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@email.com");
        user.setPassword("password");

        when(encoder.matches(anyString(),anyString())).thenReturn(false);
        when(encoder.encodePassword(anyString())).thenReturn("password");

        // Act
        User result = userService.profileFormToUser(user, form);

        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo(form.getUsername());
        assertThat(result.getPassword()).isEqualTo("password");
    }

    @Test
    public void givenProfileFormWithNewPassword_whenConvertProfileFormToUser_thenReturnUserWithNewPassword() {
        // Arrange
        ProfileForm form = new ProfileForm();
        form.setUsername("jdoes");
        form.setEmail("email@email.com");
        form.setPassword("new_password");

        User user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@email.com");
        user.setPassword("password");

        when(encoder.matches(anyString(),anyString())).thenReturn(false);
        when(encoder.encodePassword(anyString())).thenReturn("new_password");

        // Act
        User result = userService.profileFormToUser(user, form);

        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo(form.getUsername());
        assertThat(result.getPassword()).isEqualTo("new_password");
    }

    @Test
    public void givenNullId_whenConvertProfileFormToUser_thenThrowException() {
        // Assert
        assertThatThrownBy(() -> userService.profileFormToUser(null, new ProfileForm()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(UserExceptionMessage.USER_ID_IS_NULL.getMessage());
    }

    @Test
    public void givenUser_whenUpdateUser_thenReturnUser() throws UserNotFoundException {
        // Arrange
        ProfileForm form = new ProfileForm();
        form.setUsername("jdoe");
        form.setEmail("email@email.com");
        form.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@email.com");
        user.setPassword("password");

        when(userRepository.existsById(any())).thenReturn(true);
        when(userRepository.save(any())).thenReturn(user);

        // Act
        User result = userService.updateUser(user);

        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void givenEmptyUser_whenUpdateUser_thenThrowException() {
        // Assert
        assertThatThrownBy(() -> userService.updateUser(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void givenNotExistsUser_whenUpdateUser_thenThrowException() {
        // Arrange
        User user = new User();
        user.setId(100L);
        user.setUsername("jojo");
        user.setEmail("jojo@email.com");
        user.setPassword("password");

        when(userRepository.existsById(anyLong())).thenReturn(false);

        // Assert
        assertThatThrownBy(() -> userService.updateUser(user)).isInstanceOf(UserNotFoundException.class);
    }
}
