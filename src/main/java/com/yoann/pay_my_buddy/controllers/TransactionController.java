package com.yoann.pay_my_buddy.controllers;

import com.yoann.pay_my_buddy.dto.TransactionDto;
import com.yoann.pay_my_buddy.forms.TransactionForm;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller for managing transactions between users.
 */
@Controller
public class TransactionController {
    private final Logger log = LogManager.getLogger(TransactionController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    /**
     * Displays the transaction page for the authenticated user.
     *
     * @param user the currently authenticated user.
     * @param model the Spring model used to pass data to the view.
     * @return the name of the Thymeleaf view for transactions.
     */
    @GetMapping("/transaction")
    public String transaction(@AuthenticationPrincipal UserDetails user, Model model) {
        log.info("transaction view");

        try {
            User authUser = userService.getUserByEmail(user.getUsername());
            Iterable<TransactionDto> dtoTransactions = transactionService.mapTransactionsToDtoList(authUser.getSenderTransactions());
            List<User> relations = userService.getConnectedUsersFromUser(authUser);

            model.addAttribute("form", new TransactionForm());
            model.addAttribute("relations", relations);
            model.addAttribute("transactions", dtoTransactions);
        } catch (NullPointerException e) {
            log.error("User not found");
            model.addAttribute("error", "Aucun utilisateur connecté trouvé");
        } catch (Exception e) {
            log.error(e.getMessage());
            model.addAttribute("error", "User not found");
        }

        return "transaction";
    }

    /**
     * Processes the creation of a new transaction.
     *
     * @param form the form data for the transaction.
     * @param errors validation errors encountered during the form submission.
     * @param model the Spring model used to inject data in case of error.
     * @param user the currently authenticated user.
     * @param redirectAttributes attributes used to pass flash messages between redirects.
     * @return the view name to be rendered after transaction processing, either success or error.
     */
    @PostMapping("/transaction")
    public String saveTransaction(
            @Validated @ModelAttribute("form") TransactionForm form,
            BindingResult errors,
            Model model,
            @AuthenticationPrincipal UserDetails user,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Post /transaction Create new transaction");

        try {
            if (errors.hasErrors()) {
                log.error("Post /transaction errors in transaction");

                User authUser = userService.getUserByEmail(user.getUsername());
                Iterable<TransactionDto> dtoTransactions = transactionService.mapTransactionsToDtoList(authUser.getSenderTransactions());
                List<User> relations = userService.getConnectedUsersFromUser(authUser);

                model.addAttribute("relations", relations);
                model.addAttribute("transactions", dtoTransactions);

                model.addAttribute("message", "Une erreur est survenue dans le formulaire");
                model.addAttribute("message_type", "error");

                return "transaction";
            }

            User authUser = userService.getUserByEmail(user.getUsername());
            Transaction transaction = transactionService.initTransactionForAuthUser(form, authUser);
            transaction = transactionService.addTransaction(transaction);

            redirectAttributes.addFlashAttribute("receiver_username", transaction.getReceiverUser().getUsername());
            redirectAttributes.addFlashAttribute("message", "Transaction envoyée avec succès");
            redirectAttributes.addFlashAttribute("message_type", "success");

            log.info("Transaction created");
        } catch (Exception e) {
            ControllerHelper.handleBusinessError(log, redirectAttributes, e);
        }

        return "redirect:/transaction";
    }
}
