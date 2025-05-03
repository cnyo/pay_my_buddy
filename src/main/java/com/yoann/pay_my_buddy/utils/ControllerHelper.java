package com.yoann.pay_my_buddy.utils;

import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Utility class for handling controller-related helpers such as error reporting and flash messaging.
 */
public class ControllerHelper {

    /**
     * Handles a business exception by logging it and adding an error message to the redirect attributes.
     *
     * @param log                the logger used to log the error
     * @param redirectAttributes the redirect attributes used to pass flash messages to the next request
     * @param e                  the exception to handle
     */
    public static void handleBusinessError(Logger log, RedirectAttributes redirectAttributes, Exception e) {
        log.error("business error : {}", e.getMessage(), e);
        redirectAttributes.addFlashAttribute("error_message", e.getMessage());
        redirectAttributes.addFlashAttribute("message", "error");
    }
}
