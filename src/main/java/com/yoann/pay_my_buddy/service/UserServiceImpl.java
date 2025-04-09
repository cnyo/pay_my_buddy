package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.exception.SameUserInConnectionUserException;
import com.yoann.pay_my_buddy.exception.UserIsNullException;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public User addConnectionUser(User currentUser, User userToConnect) throws UserIsNullException, SameUserInConnectionUserException, IllegalArgumentException {
        log.debug("add ConnectionUser to currentUser");

        validateUsers(currentUser, userToConnect);

        ConnectionUser connectionUser = connectionUserFactory.createConnectionUser(currentUser, userToConnect);
        currentUser.addConnectionUser(connectionUser);

        return updateUser(currentUser);
    }

    private void validateUsers(User currentUser, User userToConnect) throws UserIsNullException, SameUserInConnectionUserException, IllegalArgumentException {

        if (currentUser == null) {
            log.error("currentUser is null");
            throw new UserIsNullException();
        }

        if (userToConnect == null) {
            log.error("userToConnect is null");
            throw new UserIsNullException();
        }

        if (currentUser.getId() == null || userToConnect.getId() == null) {
            log.error("id of current user or user to connect is null");
            throw new IllegalArgumentException("id of current user or user to connect is null");
        }

        if (currentUser.getId().equals(userToConnect.getId())) {
            log.error("userToConnect is the same as currentUser");
            throw new SameUserInConnectionUserException();
        }
    }
}
