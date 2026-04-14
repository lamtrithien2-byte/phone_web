package com.tdtu.cmsweb.api.dto;

import java.util.Date;

public record OnlineOrderSummaryView(
        Long id,
        String orderCode,
        String customerName,
        String recipientName,
        String recipientPhone,
        String orderStatus,
        String paymentStatus,
        Integer totalMoney,
        String voucherCode,
        Integer discountMoney,
        Date createdAt
) {
}
