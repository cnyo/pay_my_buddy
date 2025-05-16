package com.yoann.pay_my_buddy.service;

import org.springframework.stereotype.Service;

/**
 * Service interface for managing invoices.
 *
 * @property percentageToCharge the percentage to charge for the invoice
 * @property invoices the list of invoices
 */
@Service
public interface InvoiceService {
    /**
     * @return the percentage to charge for the invoice
     */
    Double getPercentageToCharge();

    /**
     * @return the list of invoices
     */
//    List<Invoice> getInvoices();

    /**
     * @param id the id of the invoice
     * @return the invoice with the given id
     */
//    Invoice getInvoiceById(id: Long);

    /**
     * @param invoice the invoice to create
     * @return the created invoice
     */
//    Invoice createInvoice(invoice: Invoice);

    /**
     * @param invoice the invoice to update
     * @return the updated invoice
     */
//    Invoice updateInvoice(invoice: Invoice);

    /**
     * @param id the id of the invoice to delete
     * @return true if the invoice was deleted, false otherwise
     */
//    Boolean deleteInvoice(id: Long);
}