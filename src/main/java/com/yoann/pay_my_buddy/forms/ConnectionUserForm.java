package com.yoann.pay_my_buddy.forms;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;

@Component
public class ConnectionUserForm {
    @NotEmpty
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
