package com.yoann.pay_my_buddy.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility component for encoding passwords using BCrypt.
 */
@Component
public class EncoderUtils {
    private final Logger log = LogManager.getLogger(EncoderUtils.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Encodes a raw password using BCrypt hashing algorithm.
     *
     * @param password the raw password to encode
     * @return the encoded password as a {@link String}
     */
    public String encodePassword(String password) {
        log.debug("Encode password");
        if (password == null || password.isEmpty()) {
            log.error("Password is null or empty");
            return null;
        }

        return encoder.encode(password);
    }

    /**
     * Checks whether a raw (plaintext) password matches the encoded (hashed) password.
     * <p>
     * This method delegates the comparison to the configured {@link PasswordEncoder},
     * typically used to verify user credentials during authentication.
     * </p>
     *
     * @param rawPassword      the plaintext password provided by the user
     * @param encodedPassword  the stored hashed password to compare against
     * @return {@code true} if the passwords match; {@code false} otherwise
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        log.debug("Check if password matches");

        return encoder.matches(rawPassword, encodedPassword);
    }
}
