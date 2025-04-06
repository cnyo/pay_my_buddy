package com.yoann.pay_my_buddy.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`user`")
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
    private List<ConnectionUser> connections = new ArrayList<>();

    @OneToMany(mappedBy = "associatedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectionUser> associatedConnections = new ArrayList<>();

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

    public void setConnections(List<ConnectionUser> connections) {
        this.connections = connections;
    }

    public List<ConnectionUser> getAssociatedConnections() {
        return associatedConnections;
    }

    public void setAssociatedConnections(List<ConnectionUser> associatedConnections) {
        this.associatedConnections = associatedConnections;
    }
}
