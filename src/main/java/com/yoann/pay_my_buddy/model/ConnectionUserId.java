package com.yoann.pay_my_buddy.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ConnectionUserId implements Serializable {
    private Long userId;
    private Long associatedUserId;

    public ConnectionUserId(Long userId, Long associatedUserId) {
        this.userId = userId;
        this.associatedUserId = associatedUserId;
    }

    public ConnectionUserId() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionUserId that)) return false;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(associatedUserId, that.associatedUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, associatedUserId);
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAssociatedUserId() {
        return associatedUserId;
    }

    public void setAssociatedUserId(Long associatedUserId) {
        this.associatedUserId = associatedUserId;
    }
}

