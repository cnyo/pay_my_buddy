package com.yoann.pay_my_buddy.forms;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class RegistrationForm extends EmailForm {
    @NotEmpty
    @Size(max = 250)
    private String username;

    @NotEmpty
    private String password;

    public RegistrationForm() {}

    public RegistrationForm(String email, String username, String password) {
        super(email);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
