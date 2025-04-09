package com.yoann.pay_my_buddy.dto;

import com.yoann.pay_my_buddy.model.Transaction;

public class TransactionDto {
    private String description;
    private double amount;
    private Long senderUserId;
    private Long receiverUserId;

    public TransactionDto(Transaction transaction) {
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.senderUserId = transaction.getSenderUser().getId();
        this.receiverUserId = transaction.getReceiverUser().getId();
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
}
