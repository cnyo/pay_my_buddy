package com.yoann.pay_my_buddy.dto;

import com.yoann.pay_my_buddy.model.Transaction;

import java.text.NumberFormat;
import java.util.Locale;

public class TransactionDto {

    private String description;
    private double amount;
    private Long senderUserId;
    private Long receiverUserId;
    private String receiverUsername;

    public TransactionDto(Transaction transaction) {
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.senderUserId = transaction.getSenderUser().getId();
        this.receiverUserId = transaction.getReceiverUser().getId();
        this.receiverUsername = transaction.getReceiverUser().getUsername();
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

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getAmountCurrency() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);

        return formatter.format(amount);
    }
}
