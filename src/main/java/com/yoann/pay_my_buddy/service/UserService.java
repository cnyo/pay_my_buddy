package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.model.User;

import java.util.Optional;

public interface UserService {
    Iterable<User> getUsers();

    Optional<User> getUser(int id);

    User addUser(User user);

    boolean removeUser(User toDeleteUser);
}
