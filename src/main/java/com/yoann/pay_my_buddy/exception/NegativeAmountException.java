package com.yoann.pay_my_buddy.exception;

public class NegativeAmountException extends IllegalArgumentException {
    public NegativeAmountException() {
        super("Amount can't be negative");
    }
}
