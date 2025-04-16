package com.yoann.pay_my_buddy.exception;

public class NegativeAmountException extends UserTransactionException {
    public NegativeAmountException() {
        super("Amount can't be negative");
    }
}
