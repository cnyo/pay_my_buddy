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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConnectionUser> connections = new HashSet<>();

    @OneToMany(mappedBy = "associatedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConnectionUser> associatedConnections = new HashSet<>();

    @OneToMany(mappedBy = "senderUser")
    private Set<Transaction> senderTransactions = new HashSet<>();

    @OneToMany(mappedBy = "receiverUser")
    private Set<Transaction> receiverTransactions = new HashSet<>();

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

    public Set<ConnectionUser> getConnections() {
        return connections;
    }

    public User addConnectionUser(ConnectionUser connectionUser) {
        if (connectionUser.getAssociatedUser() != null && connectionUser.getUser() != null) {
            connections.add(connectionUser);
        }

        return this;
    }

    public User addConnectedUser(ConnectionUser connectionUser) {
        if (connectionUser.getAssociatedUser() != null && connectionUser.getUser() != null) {
            connections.add(connectionUser);
        }

        return this;
    }

    public User removeConnectionUser(ConnectionUser connectionUser) {
        connections.remove(connectionUser);

        return this;
    }

    public void setConnections(Set<ConnectionUser> connections) {
        this.connections = connections;
    }

    public Set<ConnectionUser> getAssociatedConnections() {
        return associatedConnections;
    }

    public void setAssociatedConnections(Set<ConnectionUser> associatedConnections) {
        this.associatedConnections = associatedConnections;
    }

    public Set<Transaction> getSenderTransactions() {
        return senderTransactions;
    }

    public void setSenderTransactions(Set<Transaction> senderTransactions) {
        this.senderTransactions = senderTransactions;
    }

    public Set<Transaction> getReceiverTransactions() {
        return receiverTransactions;
    }

    public void setReceiverTransactions(Set<Transaction> receiverTransactions) {
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
