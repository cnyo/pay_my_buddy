package com.yoann.pay_my_buddy.model;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @NonNull
    @Column(name = "amount")
    private Double amount;

    @Column(name = "date")
    private Date date;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_user_id")
    private User senderUser;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_user_id")
    private User receiverUser;

    public Transaction setId(Long id) {
        this.id = id;

        return this;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Transaction setDescription(String description) {
        this.description = description;

        return this;
    }

    public Double getAmount() {
        return amount;
    }

    public Transaction setAmount(Double amount) {
        this.amount = amount;

        return this;
    }

    public Date getDate() {
        return date;
    }

    public Transaction setDate(Date date) {
        this.date = date;

        return this;
    }

    public User getSenderUser() {
        return senderUser;
    }

    public Transaction setSenderUser(User senderUser) {
        this.senderUser = senderUser;

        return this;
    }

    public User getReceiverUser() {
        return receiverUser;
    }

    public Transaction setReceiverUser(User receiverUser) {
        this.receiverUser = receiverUser;

        return this;
    }

}
