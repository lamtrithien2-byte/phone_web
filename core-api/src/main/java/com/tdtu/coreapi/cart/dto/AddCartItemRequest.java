package com.tdtu.coreapi.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @NotNull Long staffId,
        @NotNull Long productId,
        @Min(1) int quantity
) {
}
