package com.yoann.pay_my_buddy.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class EmailForm {
    @NotEmpty
    @Size(max = 250)
    @Email(message = "Email is not valid", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z0-9_]{2,}$")
    private String email;

    public EmailForm() {}

    public EmailForm(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
