package com.yoann.pay_my_buddy.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncoderUtils {
    private final Logger log = LogManager.getLogger(EncoderUtils.class);

    public String encodePassword(String password) {
        log.debug("Encode password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return encoder.encode(password);
    }
}
