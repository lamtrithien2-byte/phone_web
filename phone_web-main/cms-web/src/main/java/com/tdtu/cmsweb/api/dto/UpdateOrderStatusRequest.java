package com.tdtu.cmsweb.api.dto;

public record UpdateOrderStatusRequest(
        Long orderId,
        String orderStatus,
        String paymentStatus
) {
}
