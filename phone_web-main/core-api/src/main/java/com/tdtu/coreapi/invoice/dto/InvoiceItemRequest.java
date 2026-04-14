package com.tdtu.coreapi.invoice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InvoiceItemRequest(
        @NotNull Long productId,
        @Min(1) int quantity,
        @Min(0) int totalMoney
) {
}
