package com.yoann.pay_my_buddy.exception;

public class NullUserConnectionUserException extends ConnectionUserException {
    public NullUserConnectionUserException() {
        super("User can't be null");
    }
}
