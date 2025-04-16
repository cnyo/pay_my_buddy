package com.yoann.pay_my_buddy.utils;

import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControllerHelper {

    public static void handleBusinessError(Logger log, RedirectAttributes redirectAttributes, Exception e) {
        log.error("business error : {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("error_message", e.getMessage());
        redirectAttributes.addFlashAttribute("message", "error");
    }
}
