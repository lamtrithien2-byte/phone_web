package com.tdtu.ecommerceweb.api.dto;

import java.util.Date;

public record OrderSummaryView(
        Long id,
        String orderCode,
        String customerName,
        String orderStatus,
        String paymentStatus,
        Integer totalMoney,
        Date createdAt
) {
}
