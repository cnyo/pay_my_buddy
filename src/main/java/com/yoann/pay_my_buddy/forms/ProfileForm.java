package com.yoann.pay_my_buddy.forms;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ProfileForm extends emailForm {
    @NotEmpty
    @Size(max = 250)
    private String username;

    @NotEmpty
    private String password;

    public ProfileForm() {}

    public ProfileForm(String username, String email) {
        super(email);
        this.username = username;
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
