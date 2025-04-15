package com.yoann.pay_my_buddy.service;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.yoann.pay_my_buddy.model.User;

import java.util.List;

public interface UserService {
    Iterable<User> getUsers();

    User getUser(Long id);

    User addUser(User user);

    boolean removeUser(User toDeleteUser);

    User updateUser(User user);

    User addConnectionToUser(User currentUser, User userToConnect) throws InvalidTypeIdException;

    List<User> getConnectedUsersFromUser(User user);
}
