package com.yoann.pay_my_buddy.exception;

public class SameUserInTransactionException extends UserTransactionException {

    public SameUserInTransactionException() {
        super("Sender and receiver user can't be same");
    }
}
