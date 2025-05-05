package com.yoann.pay_my_buddy.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ConnectionUserId implements Serializable {
    private Long userId1;
    private Long userId2;

    public ConnectionUserId(Long userId1, Long userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public ConnectionUserId() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionUserId that)) return false;
        return Objects.equals(userId1, that.userId1) &&
                Objects.equals(userId2, that.userId2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId1, userId2);
    }

    public Long getUserId1() {
        return userId1;
    }

    public void setUserId1(Long userId) {
        this.userId1 = userId;
    }

    public Long getUserId2() {
        return userId2;
    }

    public void setUserId2(Long associatedUserId) {
        this.userId2 = associatedUserId;
    }
}

