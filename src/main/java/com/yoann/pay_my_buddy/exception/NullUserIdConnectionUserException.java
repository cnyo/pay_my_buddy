package com.yoann.pay_my_buddy.exception;

public class NullUserIdConnectionUserException extends ConnectionUserException {
    public NullUserIdConnectionUserException() {
        super("User ID can't be null");
    }
}
