package com.tdtu.coreapi.invoice.dto;

import java.util.Date;

public record InvoiceDetailView(
        String invoiceCode,
        Integer subtotalMoney,
        Integer discountMoney,
        Integer totalMoney,
        String voucherCode,
        String productName,
        Integer quantity,
        Integer unitPrice,
        Date createdDate
) {
}
