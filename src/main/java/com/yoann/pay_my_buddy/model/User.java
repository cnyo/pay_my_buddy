package com.yoann.pay_my_buddy.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectionUser> connections = new ArrayList<>();

    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectionUser> associatedConnections = new ArrayList<>();

    @OneToMany(mappedBy = "senderUser")
    private List<Transaction> senderTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "receiverUser")
    private List<Transaction> receiverTransactions = new ArrayList<>();

    public User setId(Long id) {
        this.id = id;

        return this;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;

        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;

        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;

        return this;
    }

    public List<ConnectionUser> getConnections() {
        return connections;
    }

    public User addConnectionUser(ConnectionUser connectionUser) {
        if (connectionUser.getUser2() != null && connectionUser.getUser1() != null) {
            connections.add(connectionUser);
        }

        return this;
    }

    public User addConnectedUser(ConnectionUser connectionUser) {
        if (connectionUser.getUser2() != null && connectionUser.getUser1() != null) {
            connections.add(connectionUser);
        }

        return this;
    }

    public User removeConnectionUser(ConnectionUser connectionUser) {
        connections.remove(connectionUser);

        return this;
    }

    public void setConnections(List<ConnectionUser> connections) {
        this.connections = connections;
    }

    public List<ConnectionUser> getAssociatedConnections() {
        return associatedConnections;
    }

    public void setAssociatedConnections(List<ConnectionUser> associatedConnections) {
        this.associatedConnections = associatedConnections;
    }

    public List<Transaction> getSenderTransactions() {
        return senderTransactions;
    }

    public void setSenderTransactions(List<Transaction> senderTransactions) {
        this.senderTransactions = senderTransactions;
    }

    public List<Transaction> getReceiverTransactions() {
        return receiverTransactions;
    }

    public void setReceiverTransactions(List<Transaction> receiverTransactions) {
        this.receiverTransactions = receiverTransactions;
    }

    // HELPERS
    public User addSenderTransactions(Transaction transaction) {
        this.senderTransactions.add(transaction);
        transaction.setSenderUser(this);

        return this;
    }

    public User removeSenderTransaction(Transaction transaction) {
        this.senderTransactions.remove(transaction);
        transaction.setSenderUser(null);

        return this;
    }

    public User addReceiverTransactions(Transaction transaction) {
        this.receiverTransactions.add(transaction);
        transaction.setSenderUser(this);

        return this;
    }

    public User removeReceiverTransaction(Transaction transaction) {
        this.receiverTransactions.remove(transaction);
        transaction.setSenderUser(null);

        return this;
    }
}
