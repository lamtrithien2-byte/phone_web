package com.tdtu.coreapi.customer.dto;

import java.util.Date;

public record PurchaseHistoryView(
        String invoiceCode,
        String salesStaffName,
        String customerName,
        Integer receiveMoney,
        Integer excessMoney,
        Integer totalMoney,
        Integer quantity,
        Date createdDate
) {
}
