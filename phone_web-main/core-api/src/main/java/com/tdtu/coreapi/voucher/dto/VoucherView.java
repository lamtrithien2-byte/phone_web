package com.tdtu.coreapi.voucher.dto;

import java.time.LocalDateTime;

public record VoucherView(
        Long id,
        String voucherCode,
        String voucherName,
        String voucherType,
        Integer discountValue,
        Boolean active,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Integer maxUsage,
        Integer usedCount,
        Integer minOrderValue,
        String effectiveStatus
) {
}
