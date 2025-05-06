package com.yoann.pay_my_buddy.dto;

import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;

import java.text.NumberFormat;
import java.util.Locale;

public class TransactionDto {
    private final NumberFormat NUMBER_FORMATTER = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    private String description;
    private double amount;
    private boolean isSender;
    private String relationUsername;

    public TransactionDto(Transaction transaction, User authUser) {
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.isSender = transaction.getSenderUser().equals(authUser);
        this.relationUsername = transaction.getConnectedUser(authUser).getUsername();
    }

    public TransactionDto() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAmountCurrency() {

        return (isSender ? "" : "-") + NUMBER_FORMATTER.format(amount);
    }

    public boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }

    public String getRelationUsername() {
        return relationUsername;
    }

    public void setRelationUsername(String relationUsername) {
        this.relationUsername = relationUsername;
    }
}
