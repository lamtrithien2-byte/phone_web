package com.tdtu.coreapi.voucher.dto;

import jakarta.validation.constraints.NotNull;

public record VoucherToggleRequest(
        @NotNull Boolean active
) {
}
