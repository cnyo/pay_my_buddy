package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.model.ConnectionUser;
import com.yoann.pay_my_buddy.model.Transaction;
import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.service.TransactionService;
import com.yoann.pay_my_buddy.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class TransactionController {
    private final Logger log = LogManager.getLogger(TransactionController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

//    @ExceptionHandler(Exception.class)

    @GetMapping("/transaction")
    public String transaction(Model model) {
//    public String transferUser(Model model, @AuthenticationPrincipal User user) {
        log.info("transaction view");
//        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
//        Iterable<User> users = userService.getUsers();
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
    public RedirectView saveTransaction(@ModelAttribute Transaction transaction, Model model) {

        log.info("Post /transaction Create new transaction: {}", transaction.getReceiverUser() != null ? transaction.getReceiverUser().getId() : "aucun receiver");

        try {
            User user = userService.getUser(1L);
            transaction.setSenderUser(user);
            log.info("Before save transaction: {}", transaction);
            log.debug("sender user id is {}", transaction.getSenderUser().getId());
//            log.debug("receiver user id is {}", transaction.getReceiverUser().getId());

            transaction = transactionService.add(transaction);

            log.info("Transaction created");
        } catch(Exception e) {
            log.error(e.getMessage());
//            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        model.addAttribute("transaction", transaction);

        return new RedirectView("transaction", true);
    }

//    @PostMapping("/transaction")
//    public RedirectView saveTransaction(@ModelAttribute Transaction transaction, Model model) {
//
//        log.info("Post /transaction Create new transaction");
//
//        try {
//            Transaction insertedTransaction = transactionService.add(transaction);
//
//            model.addAttribute("transaction", insertedTransaction);
////            Transaction transaction = transactionService.addTransaction(transactionDto, user, receiverUser);
//            log.info("Transaction created");
//        } catch(Exception e) {
//            log.error(e.getMessage());
////            redirectAttributes.addFlashAttribute("error", e.getMessage());
//        }
//
//        return new RedirectView("transfer", true);
//    }
}
