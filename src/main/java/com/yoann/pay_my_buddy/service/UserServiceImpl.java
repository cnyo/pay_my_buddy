package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.exception.*;
import com.yoann.pay_my_buddy.forms.RegistrationForm;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import com.yoann.pay_my_buddy.utils.EncoderUtils;
import com.yoann.pay_my_buddy.utils.ValidationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public Iterable<User> getUsers() {
        log.debug("Getting all users");
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        log.debug("Getting user with id");
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public User addUser(User user) {
        log.debug("add user");
        return userRepository.save(user);
    }

    @Override
    public boolean removeUser(User toDeleteUser) {
        log.debug("remove user");
        return true;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Updating user");
        return userRepository.save(user);
    }

    @Override
    public User addConnectionToUser(User currentUser, User userToConnect) throws ConnectionUserException {
        validateUsers(currentUser, userToConnect);
        log.debug("Adding connection from user {} to user {}", currentUser.getId(), userToConnect.getId());

        ConnectionUser connectionUser = connectionUserFactory.createConnectionUser(currentUser, userToConnect);
        attachConnectionToUser(connectionUser, currentUser);

        return updateUser(currentUser);
    }

    @Override
    public List<User> getConnectedUsersFromUser(User user) {
        return user.getConnections().stream().map(ConnectionUser::getAssociatedUser).toList();
    }

    public User attachConnectionToUser(ConnectionUser connectionUser, User currentUser) {
        log.debug("Attaching connection to user {}", currentUser.getId());
        currentUser.addConnectionUser(connectionUser);

        return currentUser;
    }

    private void validateUsers(User currentUser, User userToConnect) throws NullUserConnectionUserException, SameUserConnectionUserException, NullUserIdConnectionUserException {
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
}
