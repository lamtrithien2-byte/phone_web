package com.tdtu.ecommerceweb.api.dto;

import java.util.Date;

public record InvoiceDetailView(
        String invoiceCode,
        String productName,
        Integer quantity,
        Integer unitPrice,
        Date createdDate
) {
}
