package com.tdtu.coreapi.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateInvoiceRequest(
        @NotNull Long staffId,
        @NotBlank String phoneNumber,
        @NotBlank String fullName,
        String address,
        @Min(1) int quantity,
        @Min(0) int subtotalMoney,
        @Min(0) int discountMoney,
        @Min(0) int totalMoney,
        @Min(0) int receiveMoney,
        @Min(0) int moneyBack,
        String voucherCode,
        @Valid @NotEmpty List<InvoiceItemRequest> items
) {
}
