package com.tdtu.ecommerceweb.api.dto;

import java.util.List;

public record CreateOrderRequest(
        String phoneNumber,
        String fullName,
        String shippingAddress,
        String note,
        String voucherCode,
        Integer shippingFee,
        List<CreateOrderItemRequest> items
) {
}
