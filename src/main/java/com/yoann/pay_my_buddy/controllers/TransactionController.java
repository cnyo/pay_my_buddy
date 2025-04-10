package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.SameUserInTransactionException;
import com.yoann.pay_my_buddy.exception.UserNotFoundException;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.TransactionService;
import com.yoann.pay_my_buddy.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class TransactionController {
    private final Logger log = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transfer")
    public String transferUser(
            HttpServletRequest request,
            Model model,
            @AuthenticationPrincipal User user) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        model.addAttribute("user", user);
        log.info("transaction view");

        return "transaction";
    }

    @PostMapping("/transfer")
    public RedirectView transfer(
            HttpServletRequest request,
            @ModelAttribute Transaction transaction,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal User user) {

        log.info("/transaction New transaction");

        try {
//            User user = userService.getUser(Long.valueOf(id));

//            User receiverUser = userService.getUser(transaction.getSenderUserId());
            transactionService.add(transaction);
//            Transaction transaction = transactionService.addTransaction(transactionDto, user, receiverUser);
            log.info("Transaction created");
        } catch(Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return new RedirectView("transfer", true);
    }
}
