package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.exception.NullUserConnectionUserException;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Factory service for creating {@link ConnectionUser} instances.
 *
 * This class is responsible for building connections between two users,
 * including a timestamp of the connection's creation.
 */
@Service
public class ConnectionUserFactory {
    private final Logger log = LogManager.getLogger(ConnectionUserFactory.class);

    /**
     * Creates a new {@link ConnectionUser} between the given users.
     * <p>
     * This method ensures both users are not null before creating the connection.
     * If either user is null, a {@link NullUserConnectionUserException} is thrown.
     * </p>
     *
     * @param currentUser   the user initiating the connection
     * @param userToConnect the user to be connected
     * @return a new {@link ConnectionUser} instance with the current timestamp
     * @throws NullUserConnectionUserException if either {@code currentUser} or {@code userToConnect} is null
     */
    public ConnectionUser createConnectionUser(User currentUser, User userToConnect) throws NullUserConnectionUserException {
        log.debug("creating connection user");
        if (userToConnect == null || currentUser == null) {
            log.error("userToConnect or currentUser is null");
            throw new NullUserConnectionUserException();
        }

        return new ConnectionUser(currentUser, userToConnect, new Date());
    }
}
