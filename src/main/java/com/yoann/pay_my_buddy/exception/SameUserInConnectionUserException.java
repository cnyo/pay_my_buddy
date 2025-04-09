package com.yoann.pay_my_buddy.exception;

public class SameUserInConnectionUserException extends RuntimeException{
    public SameUserInConnectionUserException() {
        super("Users can't be same for connection");
    }
}
