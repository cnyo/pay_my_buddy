package com.yoann.pay_my_buddy.exception;

import org.hibernate.TransactionException;

public class NullUserTransactionException extends TransactionException {
    public NullUserTransactionException() {
        super("User can't be null");
    }
}
