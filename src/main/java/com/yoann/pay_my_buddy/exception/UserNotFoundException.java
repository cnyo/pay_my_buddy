package com.yoann.pay_my_buddy.exception;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() {
        super("User not found");
    }
}
