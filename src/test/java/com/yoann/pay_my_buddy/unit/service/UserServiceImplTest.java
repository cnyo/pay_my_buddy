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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {

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
    public void whenGetUser_thenReturnNull() {
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
        User user = new User();
        List<User> users = new ArrayList<>();
        users.add(user);

        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        Iterable<User> result = userService.getUsers();

        assertThat(result).isInstanceOf(List.class);
        assertThat(result).isEmpty();
    }

    @Test
    public void whenAddUser_thenReturnUser() {
        User user = new User();
        user.setUsername("username").setEmail("email@mail.com").setPassword("password");
        User savedUser = new User();
        savedUser.setId(5L)
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());

        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.addUser(user);

        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(User.class);
        assertThat(result.getUsername()).isEqualTo("username");
    }

    @Test
    public void addUser_whenUserAlreadyExists_thenReturnUser() {
//        User user = new User();
//        user.setUsername("username").setEmail("email@mail.com").setPassword("password");
//
//        em.persist(user);
//        // em.flush();
//
//        User newUser = new User();
//        newUser.setId(5L)
//                .setUsername(user.getUsername())
//                .setEmail(user.getEmail())
//                .setPassword(user.getPassword());
//
//        when(userRepository.save(user)).thenReturn(newUser);
//
//        User result = userService.addUser(user);
//
//        assertThat(result).isNotNull();
        //assertThat(result).isInstanceOf(User.class);
        //assertThat(result.getUsername()).isEqualTo("username");
    }

    @Test
    public void addUser_whenUserWithoutUsername_thenReturnUser() {

    }

    @Test
    public void whenUpdateUser_thenReturnUser() {

    }

    @Test
    public void whenDeleteUser_thenReturnTrue() {

    }

    @Test
    public void whenDeleteUser_thenReturnFalse() {

    }
}
