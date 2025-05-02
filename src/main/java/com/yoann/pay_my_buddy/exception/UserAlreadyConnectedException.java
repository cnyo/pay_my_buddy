package com.yoann.pay_my_buddy.exception;

public class UserAlreadyConnectedException extends ConnectionUserException {
    public UserAlreadyConnectedException() {
        super("User already connected");
    }
}
