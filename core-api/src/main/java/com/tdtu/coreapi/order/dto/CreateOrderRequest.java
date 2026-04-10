package com.tdtu.coreapi.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank String phoneNumber,
        @NotBlank String fullName,
        @NotBlank String shippingAddress,
        String note,
        @Min(0) Integer shippingFee,
        @Valid @NotEmpty List<CreateOrderItemRequest> items
) {
}
