package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.exception.*;
import com.yoann.pay_my_buddy.forms.ProfileForm;
import com.yoann.pay_my_buddy.forms.RegistrationForm;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

/**
 * Interface for managing {@link User} operations.
 */
public interface UserService {

    /**
     * Retrieves all users.
     *
     * @return an {@link Iterable} of {@link User} entities.
     */
    Iterable<User> getUsers();

    /**
     * Retrieves a user by ID.
     *
     * @param id the user ID.
     * @return the corresponding {@link User}.
     * @throws EntityNotFoundException if no user with the given ID exists.
     */
    User getUser(Long id);

    /**
     * Adds a new user to the database.
     *
     * @param user the {@link User} to add.
     * @return the saved {@link User}.
     */
    User addUser(User user);

    /**
     * Updates a user in the database.
     *
     * @param user the user to update.
     * @return the updated {@link User}.
     * @throws IllegalArgumentException if the user is null.
     * @throws UserNotFoundException if the user does not exist.
     */
    User updateUser(User user) throws IllegalArgumentException, UserNotFoundException;

    /**
     * Updates user data based on a profile form.
     *
     * @param user the existing {@link User}.
     * @param form the {@link ProfileForm} containing updated values.
     * @return the updated {@link User}.
     * @throws IllegalArgumentException if the user is null.
     * @throws NullPointerException if the form is null.
     */
    User profileFormToUser(User user, ProfileForm form) throws IllegalArgumentException, NullPointerException;

    /**
     * Adds a connection between two users.
     *
     * @param currentUser the authenticated user.
     * @param userToConnect the user to connect to.
     * @return the updated {@link User} with the new connection.
     * @throws ConnectionUserException if the connection is invalid or already exists.
     */
    User addConnectionToUser(User currentUser, User userToConnect) throws ConnectionUserException;

    /**
     * Returns all users connected to the given user.
     *
     * @param user the user whose connections are retrieved.
     * @return list of connected {@link User} entities.
     */
    List<User> getConnectedUsersFromUser(User user);

    /**
     * Attaches a connection to the current user.
     *
     * @param connectionUser the {@link ConnectionUser} object to attach.
     * @param currentUser the current user.
     * @return the updated {@link User}.
     */
    User attachConnectionToUser(ConnectionUser connectionUser, User currentUser);

    /**
     * Validates the users involved in a connection.
     *
     * @param currentUser the authenticated user.
     * @param userToConnect the user to connect to.
     * @throws NullUserConnectionUserException if either user is null.
     * @throws SameUserConnectionUserException if both users are the same.
     * @throws NullUserIdConnectionUserException if either user has a null ID.
     */
    void validateUsers(User currentUser, User userToConnect) throws NullUserConnectionUserException, SameUserConnectionUserException, NullUserIdConnectionUserException;

    /**
     * Retrieves a user by email.
     *
     * @param email the email to search by.
     * @return the {@link User} found.
     * @throws UserNotFoundException if no user with the given email exists.
     */
    User getUserByEmail(String email) throws UserNotFoundException;

    /**
     * Initializes a new {@link User} based on a registration form.
     *
     * @param form the {@link RegistrationForm} containing user data.
     * @return the initialized {@link User}.
     * @throws BadRegistrationDataException if the email is invalid.
     */
    User initUserFromRegistrationForm(RegistrationForm form) throws BadRegistrationDataException;

    /**
     * Retrieves a user by username.
     *
     * @param username the username to search.
     * @return the {@link User} found.
     * @throws IllegalArgumentException if the username is null.
     * @throws NullPointerException if no user with the username exists.
     */
    User getUserByUsername(String username) throws IllegalArgumentException, NullPointerException;
}
