package com.yoann.pay_my_buddy.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility component for encoding passwords using BCrypt.
 */
@Component
public class EncoderUtils {
    private final Logger log = LogManager.getLogger(EncoderUtils.class);

    /**
     * Encodes a raw password using BCrypt hashing algorithm.
     *
     * @param password the raw password to encode
     * @return the encoded password as a {@link String}
     */
    public String encodePassword(String password) {
        log.debug("Encode password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.encode(password);
    }
}
