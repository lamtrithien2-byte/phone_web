package com.tdtu.cmsweb.api.dto;

public record InvoiceCreatedView(
        Long invoiceId,
        String invoiceCode,
        Long customerId,
        String pdfLink,
        Integer subtotalMoney,
        Integer discountMoney,
        Integer totalMoney,
        String voucherCode
) {
}
