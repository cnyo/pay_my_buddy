package com.yoann.pay_my_buddy.enums;

public enum UserExceptionMessage {
    USER_NOT_FOUND("User not found"),
    USERNAME_IS_EMPTY("Username must not be empty"),
    USER_ID_IS_NULL("Userid must not be null"),
    USER_IS_NULL("User must not be null"),
    USER_PROFILE_FORM_IS_NULL("ProfileForm must not be null");

    private final String message;

    UserExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
