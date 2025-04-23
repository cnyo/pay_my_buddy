package com.yoann.pay_my_buddy.exception;

public class BadProfileDataException extends Exception {
    public BadProfileDataException(String message) {
        super(message);
    }

    public BadProfileDataException() {
        super("Bad profile Data");
    }
}
