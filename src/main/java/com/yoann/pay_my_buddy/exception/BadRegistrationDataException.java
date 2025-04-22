package com.yoann.pay_my_buddy.exception;

public class BadRegistrationDataException extends Exception {
    public BadRegistrationDataException(String message) {
        super(message);
    }

    public BadRegistrationDataException() {
        super("Bad Registration Data");
    }
}
