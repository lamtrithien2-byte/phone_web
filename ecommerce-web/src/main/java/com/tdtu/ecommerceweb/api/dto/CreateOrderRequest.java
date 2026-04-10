package com.tdtu.ecommerceweb.api.dto;

import java.util.List;

public record CreateOrderRequest(
        String phoneNumber,
        String fullName,
        String shippingAddress,
        String note,
        Integer shippingFee,
        List<CreateOrderItemRequest> items
) {
}
