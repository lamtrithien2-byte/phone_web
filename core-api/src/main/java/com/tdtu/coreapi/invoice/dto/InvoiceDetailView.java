package com.tdtu.coreapi.invoice.dto;

import java.util.Date;

public record InvoiceDetailView(
        String invoiceCode,
        String productName,
        Integer quantity,
        Integer unitPrice,
        Date createdDate
) {
}
