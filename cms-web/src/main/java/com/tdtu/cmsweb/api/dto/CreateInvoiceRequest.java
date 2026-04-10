package com.tdtu.cmsweb.api.dto;

import java.util.List;

public record CreateInvoiceRequest(
        Long staffId,
        String phoneNumber,
        String fullName,
        String address,
        int quantity,
        int totalMoney,
        int receiveMoney,
        int moneyBack,
        List<InvoiceItemRequest> items
) {
}
