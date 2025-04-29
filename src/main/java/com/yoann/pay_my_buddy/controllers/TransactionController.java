package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.exception.UserTransactionException;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.TransactionService;
import com.yoann.pay_my_buddy.service.UserService;
import com.yoann.pay_my_buddy.utils.ControllerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class TransactionController {
    private final Logger log = LogManager.getLogger(TransactionController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String transaction(Model model) {
        log.info("transaction view");

        Iterable<Transaction> transactions = transactionService.getTransactions();
        Iterable<TransactionDto> dtoTransactions = transactionService.mapTransactionsToDtoList(transactions);

        User user = userService.getUser(1L);
        List<User> relations = userService.getConnectedUsersFromUser(user);

        model.addAttribute("transaction", new Transaction());
        model.addAttribute("user", user);
        model.addAttribute("relations", relations);
        model.addAttribute("transactions", dtoTransactions);

        return "transaction";
    }

    @PostMapping("/transaction")
    public RedirectView saveTransaction(@Validated Transaction transaction, Errors errors, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        log.info("Post /transaction Create new transaction: {}", transaction.getReceiverUser() != null ? transaction.getReceiverUser().getId() : "aucun receiver");

        if (errors.hasErrors()) {
            log.error("Post /transaction errors in transaction");
            return new RedirectView("/");
        }

        try {
            User authUser = userService.getUserByEmail(userDetails.getUsername());
            transaction.setSenderUser(authUser);
            transaction = transactionService.addTransaction(transaction);

            redirectAttributes.addFlashAttribute("receiver_username", transaction.getReceiverUser().getUsername());
            redirectAttributes.addFlashAttribute("message", "success");

            log.info("Transaction created");
        } catch (UserTransactionException e) {
            ControllerHelper.handleBusinessError(log, redirectAttributes, e);
        } catch (Exception e) {
            log.error("technical error has occurred", e);
            redirectAttributes.addFlashAttribute("error_message", "A technical error has occurred");
            redirectAttributes.addFlashAttribute("message", "success");
        }

        return new RedirectView("/", true);
    }
}
