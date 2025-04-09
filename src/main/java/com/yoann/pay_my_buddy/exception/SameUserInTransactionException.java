package com.yoann.pay_my_buddy.exception;

public class SameUserInTransactionException extends IllegalArgumentException {
    private static final String DEFAULT_MESSAGE = "Sender and reciver user can't be same";

    public SameUserInTransactionException() {
        super(DEFAULT_MESSAGE);
    }
}
