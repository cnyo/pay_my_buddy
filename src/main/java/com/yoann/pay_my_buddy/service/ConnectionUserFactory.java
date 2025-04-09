package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.exception.UserIsNullException;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ConnectionUserFactory {
    private final Logger log = LogManager.getLogger(ConnectionUserFactory.class);

    public ConnectionUser createConnectionUser(User currentUser, User userToConnect) {
        log.debug("creating connection user");
        if (userToConnect == null || currentUser == null) {
            log.error("userToConnect or currentUser is null");
            throw new UserIsNullException();
        }

        return new ConnectionUser(currentUser, userToConnect, new Date());
    }
}
