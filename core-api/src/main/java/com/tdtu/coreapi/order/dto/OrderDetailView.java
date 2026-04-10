package com.tdtu.coreapi.order.dto;

import java.util.Date;

public record OrderDetailView(
        String orderCode,
        String orderStatus,
        String paymentStatus,
        Integer totalMoney,
        String shippingAddress,
        String productName,
        Integer quantity,
        Integer unitPrice,
        Integer lineTotal,
        Date createdAt
) {
}
