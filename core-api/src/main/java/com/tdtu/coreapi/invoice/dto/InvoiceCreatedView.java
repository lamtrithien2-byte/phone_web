package com.tdtu.coreapi.invoice.dto;

public record InvoiceCreatedView(
        Long invoiceId,
        String invoiceCode,
        Long customerId,
        String pdfLink
) {
}
