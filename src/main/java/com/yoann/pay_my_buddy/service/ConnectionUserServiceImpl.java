package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.User;
import org.springframework.stereotype.Service;

@Service
public class ConnectionUserServiceImpl implements ConnectionUserService {
    public ConnectionUser newConnectionWithUser(User user) {
        ConnectionUser connectionUser = new ConnectionUser();
        connectionUser.setAssociatedUser(user);

        return connectionUser;
    }
}
