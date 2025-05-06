package com.yoann.pay_my_buddy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "connection_users")
@IdClass(ConnectionUserId.class)
public class ConnectionUser {

    @Id
    @Column(name = "user_id_1")
    private Long userId1;

    @Id
    @Column(name = "user_id_2")
    private Long userId2;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id_1", nullable = false, insertable = false, updatable = false)
    @MapsId("userId1")
    private User user1;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id_2", insertable = false, updatable = false)
    @MapsId("userId2")
    private User user2;

    @JoinColumn(nullable = false)
    private Date date;

    public ConnectionUser() {}

    public ConnectionUser(User user, User associatedUser, Date date) {
        this.user1 = user;
        this.user2 = associatedUser;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public ConnectionUser setDate(Date date) {
        this.date = date;

        return this;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user) {
        this.user1 = user;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User associatedUser) {
        this.user2 = associatedUser;
    }
}
