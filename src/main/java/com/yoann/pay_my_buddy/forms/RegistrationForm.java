package com.yoann.pay_my_buddy.forms;

import jakarta.validation.constraints.NotEmpty;

public class RegistrationForm extends emailForm {
    @NotEmpty
    private String password;

    public RegistrationForm() {}

    public RegistrationForm(String email, String password) {
        super(email);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
