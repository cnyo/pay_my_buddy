package com.yoann.pay_my_buddy.exception;

public class SameUserTransactionException extends UserTransactionException {

    public SameUserTransactionException() {
        super("Sender and receiver user can't be same");
    }
}
