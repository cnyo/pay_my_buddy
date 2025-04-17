package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.exception.ConnectionUserException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.forms.RegistrationForm;
import com.yoann.pay_my_buddy.model.User;

import java.util.List;

public interface UserService {
    Iterable<User> getUsers();

    User getUser(Long id);

    User addUser(User user);

    boolean removeUser(User toDeleteUser);

    User updateUser(User user);

    User addConnectionToUser(User currentUser, User userToConnect) throws ConnectionUserException;

    List<User> getConnectedUsersFromUser(User user);

    User getUserByEmail(String email) throws UserNotFoundException;

    User initUserFromRegistrationForm(RegistrationForm form);
}
