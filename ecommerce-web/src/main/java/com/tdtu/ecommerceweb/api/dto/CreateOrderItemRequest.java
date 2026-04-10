package com.tdtu.ecommerceweb.api.dto;

public record CreateOrderItemRequest(
        Long productId,
        Integer quantity,
        Integer unitPrice
) {
}
