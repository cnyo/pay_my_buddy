package com.yoann.pay_my_buddy.exception;

public class UserIsNullException extends NullPointerException{
    public UserIsNullException() {
        super("User can't be null");
    }
}
