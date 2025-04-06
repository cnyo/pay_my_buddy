package com.yoann.pay_my_buddy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
@IdClass(ConnectionUserId.class)
public class ConnectionUser {

//    @EmbeddedId
//    private ConnectionUserId id;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "associated_user_id")
    private Long associatedUserId;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    @MapsId("userId")
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "associated_user_id", insertable = false, updatable = false)
    @MapsId("associatedUserId")
    private User associatedUser;

    @JoinColumn(nullable = false)
    private Date date;

    public ConnectionUser() {}

    public ConnectionUser(User user, User associatedUser, Date date) {
        this.user = user;
        this.associatedUser = associatedUser;
        this.date = date;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == null) {
//            return false;
//        }
//
//        if (this == o) {
//            return true;
//        }
//
//        if (!(o instanceof ConnectionUser other)) {
//            return false;
//        }
//
//        return this.user.getId().equals(other.getUser().getId()) && this.associatedUser.getId().equals(other.getAssociatedUser().getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(user.getId(), associatedUser.getId());
//    }

    public Date getDate() {
        return date;
    }

    public ConnectionUser setDate(Date date) {
        this.date = date;

        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAssociatedUser() {
        return associatedUser;
    }

    public void setAssociatedUser(User associatedUser) {
        this.associatedUser = associatedUser;
    }
}
