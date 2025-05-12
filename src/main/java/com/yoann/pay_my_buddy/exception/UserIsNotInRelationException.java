package com.yoann.pay_my_buddy.exception;

public class UserIsNotInRelationException extends Exception {
    public UserIsNotInRelationException(String message) {
        super(message);
    }

    public UserIsNotInRelationException() {
        super("User is not in relation");
    }
}
