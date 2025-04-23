package com.yoann.pay_my_buddy.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super("User not found");
    }
}
