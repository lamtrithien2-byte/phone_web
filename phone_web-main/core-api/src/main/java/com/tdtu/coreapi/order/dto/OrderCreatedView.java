package com.tdtu.coreapi.order.dto;

public record OrderCreatedView(
        Long orderId,
        String orderCode,
        Long customerId,
        Integer subtotalMoney,
        Integer discountMoney,
        Integer totalMoney
) {
}
