package com.yoann.pay_my_buddy.utils;

import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for user input.
 */
public class ValidationUtils {
    private final static String EMAIL_PATTERN_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z0-9_]{2,}$";

    /**
     * Validates whether the given email address is in a valid format.
     *
     * @param email the email address to validate
     * @return {@code true} if the email format is valid, {@code false} otherwise
     */
    public static boolean emailIsValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN_REGEX, Pattern.CASE_INSENSITIVE);

        return email != null && pattern.matcher(email).matches();
    }
}
