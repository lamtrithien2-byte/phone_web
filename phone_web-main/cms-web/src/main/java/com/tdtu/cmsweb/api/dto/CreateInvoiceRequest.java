package com.tdtu.cmsweb.api.dto;

import java.util.List;

public record CreateInvoiceRequest(
        Long staffId,
        String phoneNumber,
        String fullName,
        String address,
        int quantity,
        int subtotalMoney,
        int discountMoney,
        int totalMoney,
        int receiveMoney,
        int moneyBack,
        String voucherCode,
        List<InvoiceItemRequest> items
) {
}
