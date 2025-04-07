package com.yoann.pay_my_buddy.unit.service;

import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import com.yoann.pay_my_buddy.service.UserService;
import com.yoann.pay_my_buddy.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {

    @Autowired
    private TestEntityManager em;

    @InjectMocks
    private static UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeAll
    public static void setUp() {
        userService = new UserServiceImpl();
    }

    @Test
    public void whenGetUser_thenReturnUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(new User()));

        Optional<User> result = userService.getUser(1);

        assertThat(result).isPresent();
        assertThat(result.get()).isInstanceOf(User.class);
    }

    @Test
    public void whenGetNoneExistsUser_thenReturnNull() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<User> result = userService.getUser(1);

        assertThat(result).isEmpty();
    }

    @Test
    public void whenGetUsers_thenReturnList() {
        User user = new User();
        List<User> users = new ArrayList<>();
        users.add(user);

        when(userRepository.findAll()).thenReturn(users);

        Iterable<User> result = userService.getUsers();

        assertThat(result).isInstanceOf(List.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    public void whenGetUsers_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        Iterable<User> result = userService.getUsers();

        assertThat(result).isInstanceOf(List.class);
        assertThat(result).isEmpty();
    }

    @Test
    public void givenNewUser_whenAdd_thenReturnUser() {
        User newUser = new User();
        newUser.setId(anyLong()).setUsername("username").setEmail("email@mail.com").setPassword("password");

        when(userRepository.save(any())).thenReturn(newUser);

        User result = userService.addUser(newUser);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getUsername()).isEqualTo("username");
    }

    @Test
    public void givenUser_whenUpdate_thenReturnUser() {
        User updateUser = new User();
        updateUser.setId(40L).setUsername("username").setEmail("email@mail.com").setPassword("password");

        when(userRepository.save(any())).thenReturn(updateUser);

        User result = userService.addUser(updateUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(40L);
        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getUsername()).isEqualTo("username");
    }

    @Test
    public void whenDeleteUser_thenReturnTrue() {
        User toDeleteUser = new User();
        toDeleteUser.setId(40L).setUsername("username").setEmail("email@mail.com").setPassword("password");

        boolean result = userService.removeUser(toDeleteUser);

        assertThat(result).isTrue();
    }

    @Test
    public void whenDeleteUser_thenReturnFalse() {
        User toDeleteUser = new User();
        toDeleteUser.setId(40L).setUsername("username").setEmail("email@mail.com").setPassword("password");

        boolean result = userService.removeUser(toDeleteUser);

        assertThat(result).isTrue();
    }
}
