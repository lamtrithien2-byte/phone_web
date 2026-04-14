package com.tdtu.coreapi.voucher.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VoucherUpsertRequest(
        @NotBlank String voucherCode,
        @NotBlank String voucherName,
        @NotBlank String voucherType,
        @NotNull @Min(0) Integer discountValue,
        @NotNull Boolean active,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        @NotNull @Min(0) Integer maxUsage,
        @NotNull @Min(0) Integer minOrderValue
) {
}
