package com.tdtu.coreapi.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull Long orderId,
        @NotBlank String orderStatus,
        @NotBlank String paymentStatus
) {
}
