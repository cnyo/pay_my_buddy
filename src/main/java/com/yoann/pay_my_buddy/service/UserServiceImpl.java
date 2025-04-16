package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.exception.SameUserInConnectionUserException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionUserFactory connectionUserFactory;

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
    public User addConnectionToUser(User currentUser, User userToConnect) throws NullPointerException, SameUserInConnectionUserException, IllegalArgumentException {
        log.debug("Adding connection from user {} to user {}", currentUser.getId(), userToConnect.getId());

        validateUsers(currentUser, userToConnect);

        ConnectionUser connectionUser = connectionUserFactory.createConnectionUser(currentUser, userToConnect);
        attachConnectionToUser(currentUser, connectionUser);

        return updateUser(currentUser);
    }

    @Override
    public List<User> getConnectedUsersFromUser(User user) {
        return user.getConnections().stream().map(ConnectionUser::getAssociatedUser).toList();
    }

    public void attachConnectionToUser(User currentUser, ConnectionUser connectionUser) {
        log.debug("Attaching connection to user {}", currentUser.getId());
        currentUser.addConnectionUser(connectionUser);
    }

    private void validateUsers(User currentUser, User userToConnect) throws NullPointerException, SameUserInConnectionUserException, IllegalArgumentException {
        Objects.requireNonNull(currentUser, "currentUser must not be null");
        Objects.requireNonNull(userToConnect, "currentUser must not be null");

        if (currentUser.getId() == null || userToConnect.getId() == null) {
            log.error("id of current user or user to connect is null");
            throw new IllegalArgumentException("id of current user or user to connect is null");
        }

        if (currentUser.getId().equals(userToConnect.getId())) {
            log.error("userToConnect is the same as currentUser");
            throw new SameUserInConnectionUserException();
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
}
