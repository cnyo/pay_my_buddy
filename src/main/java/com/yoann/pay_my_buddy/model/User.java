package com.yoann.pay_my_buddy.model;

import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Stream;

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

    @OneToMany(mappedBy = "user1", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConnectionUser> user1Connections = new ArrayList<>();

    @OneToMany(mappedBy = "user2", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConnectionUser> user2Connections = new ArrayList<>();

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

    public List<ConnectionUser> getUser1Connections() {
        return user1Connections;
    }

    public User addConnectionUser(ConnectionUser connectionUser) {
        if (connectionUser.getUser2() != null && connectionUser.getUser1() != null) {
            user1Connections.add(connectionUser);
        }

        return this;
    }

    public User addConnectedUser(ConnectionUser connectionUser) {
        if (connectionUser.getUser2() != null && connectionUser.getUser1() != null) {
            user1Connections.add(connectionUser);
        }

        return this;
    }

    public User removeConnectionUser(ConnectionUser connectionUser) {
        user1Connections.remove(connectionUser);

        return this;
    }

    public void setUser1Connections(List<ConnectionUser> connections) {
        this.user1Connections = connections;
    }

    public List<ConnectionUser> getUser2Connections() {
        return user2Connections;
    }

    public void setUser2Connections(List<ConnectionUser> associatedConnections) {
        this.user2Connections = associatedConnections;
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

    public boolean isInRelationWith(User user) {
        Long userId = user.getId();

        if (userId == null) {
            return false;
        }

        if (this.getId().equals(userId)) {
            return false;
        }

        return Stream.concat(user1Connections.stream(), user2Connections.stream()).anyMatch(
                connectionUser -> connectionUser.getUser1().getId().equals(user.getId()) ||
                        connectionUser.getUser2().getId().equals(user.getId())
        );

    }
}
