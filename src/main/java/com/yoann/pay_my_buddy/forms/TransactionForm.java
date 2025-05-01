package com.yoann.pay_my_buddy.forms;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransactionForm {
    @NotEmpty
    private String description;

    @NotNull
    @Positive
    private String amount;

    @NotNull
    @Positive
    private String receiverUserId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public Double getAmountAsDouble() {
        return Double.valueOf(amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public Long getReceiverUserIdAsLong() {
        return Long.valueOf(receiverUserId);
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }
}
