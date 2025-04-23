package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.exception.BadRegistrationDataException;
import com.yoann.pay_my_buddy.exception.ConnectionUserException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.forms.ProfileForm;
import com.yoann.pay_my_buddy.forms.RegistrationForm;
import com.yoann.pay_my_buddy.model.User;

import java.util.List;

public interface UserService {
    Iterable<User> getUsers();

    User getUser(Long id);

    User addUser(User user);

    User updateUser(User user) throws UserNotFoundException, BadRegistrationDataException;

    User updateUserFromProfileForm(Long id, ProfileForm form) throws UserNotFoundException, BadRegistrationDataException;

    User addConnectionToUser(User currentUser, User userToConnect) throws ConnectionUserException;

    List<User> getConnectedUsersFromUser(User user);

    User getUserByEmail(String email) throws UserNotFoundException;

    User initUserFromRegistrationForm(RegistrationForm form) throws BadRegistrationDataException;

    User getUserByUsername(String username) throws UserNotFoundException;
}
