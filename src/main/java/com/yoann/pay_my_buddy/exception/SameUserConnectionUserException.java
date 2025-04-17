package com.yoann.pay_my_buddy.exception;

public class SameUserConnectionUserException extends ConnectionUserException {
    public SameUserConnectionUserException() {
        super("Users can't be same for connection");
    }
}
