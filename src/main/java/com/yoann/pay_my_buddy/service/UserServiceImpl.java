package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.enums.UserExceptionMessage;
import com.yoann.pay_my_buddy.exception.*;
import com.yoann.pay_my_buddy.forms.ProfileForm;
import com.yoann.pay_my_buddy.forms.RegistrationForm;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import com.yoann.pay_my_buddy.utils.EncoderUtils;
import com.yoann.pay_my_buddy.utils.ValidationUtils;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing users and their connections.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionUserFactory connectionUserFactory;

    @Autowired
    EncoderUtils encoder;

    private final Logger log = LogManager.getLogger(UserServiceImpl.class);

    /**
     * Retrieves all users.
     *
     * @return an {@link Iterable} of {@link User} entities.
     */
    @Override
    public Iterable<User> getUsers() {
        log.debug("Getting all users");
        return userRepository.findAll();
    }

    /**
     * Adds a new user to the database.
     *
     * @param user the {@link User} to add.
     * @return the saved {@link User}.
     */
    @Override
    public User addUser(User user) {
        log.debug("add user");
        return userRepository.save(user);
    }

    /**
     * Updates a user in the database.
     *
     * @param user the user to update.
     * @return the updated {@link User}.
     * @throws IllegalArgumentException if the user is null.
     * @throws UserNotFoundException if the user does not exist.
     */
    @Override
    public User updateUser(User user) throws IllegalArgumentException, UserNotFoundException {
        log.debug("Updating user");

        if (user == null) {
            log.debug("user is null");
            throw new IllegalArgumentException(UserExceptionMessage.USER_IS_NULL.getMessage());
        }

        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
        }

        return userRepository.save(user);
    }

    /**
     * Updates a {@link User} entity using the data from a {@link ProfileForm}.
     * <p>
     * This method checks that both the user and the form are non-null, then updates the user's
     * username, email, and password (if the new password is not blank and has changed).
     * </p>
     *
     * @param user the existing user entity to be updated
     * @param form the profile form containing new data
     * @return the updated user entity
     * @throws IllegalArgumentException if the user is {@code null}
     * @throws NullPointerException     if the form is {@code null}
     */
    @Override
    public User profileFormToUser(User user, ProfileForm form) throws IllegalArgumentException, NullPointerException {
        log.debug("Convert ProfileForm to user");

        if (user == null) {
            log.error("id is null");
            throw new IllegalArgumentException(UserExceptionMessage.USER_ID_IS_NULL.getMessage());
        }

        if (form == null) {
            log.error("form is null");
            throw new NullPointerException(UserExceptionMessage.USER_PROFILE_FORM_IS_NULL.getMessage());
        }

        if (!StringUtils.isBlank(form.getPassword()) && !encoder.matches(form.getPassword(), user.getPassword())) {
            String encodedPassword = encoder.encodePassword(form.getPassword());
            user.setPassword(encodedPassword);
        }

        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());

        log.debug("ProfileForm converted to user successfully");

        return user;
    }

    /**
     * Adds a connection between two users.
     *
     * @param currentUser the authenticated user.
     * @param userToConnect the user to connect to.
     * @return the updated {@link User} with the new connection.
     * @throws ConnectionUserException if the connection is invalid or already exists.
     */
    @Override
    public User addConnectionToUser(User currentUser, User userToConnect) throws ConnectionUserException {
        validateUsers(currentUser, userToConnect);
        log.debug("Adding connection from user {} to user {}", currentUser.getId(), userToConnect.getId());

        if (currentUser.getUser1Connections().stream().anyMatch(c -> c.getUser2().getId().equals(userToConnect.getId()))) {
            log.error("user already connected");
            throw new UserAlreadyConnectedException();
        }

        ConnectionUser connectionUser = connectionUserFactory.createConnectionUser(currentUser, userToConnect);
        attachConnectionToUser(connectionUser, currentUser);

        return userRepository.save(currentUser);
    }

    /**
     * Returns all users connected to the given user.
     *
     * @param user the user whose connections are retrieved.
     * @return list of connected {@link User} entities.
     */
    @Override
    public List<User> getConnectedUsersFromUser(User user) {
        return userRepository.getConnectedUsersFromUser(user.getId());
    }

    /**
     * Attaches a connection to the current user.
     *
     * @param connectionUser the {@link ConnectionUser} object to attach.
     * @param currentUser the current user.
     * @return the updated {@link User}.
     */
    @Override
    public User attachConnectionToUser(ConnectionUser connectionUser, User currentUser) {
        log.debug("Attaching connection to user {}", currentUser.getId());
        currentUser.addConnectionUser(connectionUser);

        return currentUser;
    }

    /**
     * Validates the users involved in a connection.
     *
     * @param currentUser the authenticated user.
     * @param userToConnect the user to connect to.
     * @throws NullUserConnectionUserException if either user is null.
     * @throws SameUserConnectionUserException if both users are the same.
     * @throws NullUserIdConnectionUserException if either user has a null ID.
     */
    @Override
    public void validateUsers(User currentUser, User userToConnect) throws NullUserConnectionUserException, SameUserConnectionUserException, NullUserIdConnectionUserException {
        if (currentUser == null || userToConnect == null) {
            log.error("id of current user or user to connect is null");
            throw new NullUserConnectionUserException();
        }

        if (currentUser.getId() == null || userToConnect.getId() == null) {
            log.error("id of current user or user to connect is null");
            throw new NullUserIdConnectionUserException();
        }

        if (currentUser.getId().equals(userToConnect.getId())) {
            log.error("userToConnect is the same as currentUser");
            throw new SameUserConnectionUserException();
        }
    }

    /**
     * Retrieves a user by email.
     *
     * @param email the email to search by.
     * @return the {@link User} found.
     * @throws UserNotFoundException if no user with the given email exists.
     */
    @Override
    public User getUserByEmail(String email) throws UserNotFoundException {
        log.debug("Getting user by email");
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            log.error("user not found");
            throw new UserNotFoundException();
        }

        return user;
    }

    /**
     * Initializes a new {@link User} based on a registration form.
     *
     * @param form the {@link RegistrationForm} containing user data.
     * @return the initialized {@link User}.
     * @throws BadRegistrationDataException if the email is invalid.
     */
    @Override
    public User initUserFromRegistrationForm(RegistrationForm form) throws BadRegistrationDataException {
        log.debug("Init user from registration form");

        if (!ValidationUtils.emailIsValid(form.getEmail())) {
            log.error("email {} is invalid", form.getEmail());
            throw new BadRegistrationDataException("Email is invalid");
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(encoder.encodePassword(form.getPassword()));

        return user;
    }

    /**
     * Retrieves a user by username.
     *
     * @param username the username to search.
     * @return the {@link User} found.
     * @throws IllegalArgumentException if the username is null.
     * @throws NullPointerException if no user with the username exists.
     */
    @Override
    public User getUserByUsername(String username) throws IllegalArgumentException, NullPointerException {
        if (username == null) {
            throw new IllegalArgumentException(UserExceptionMessage.USERNAME_IS_EMPTY.getMessage());
        }

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new NullPointerException(UserExceptionMessage.USER_NOT_FOUND.getMessage());
        }

        return user.get();
    }
}
