package com.tdtu.cmsweb.api.dto;

import java.time.LocalDateTime;

public record VoucherUpsertRequest(
        String voucherCode,
        String voucherName,
        String voucherType,
        Integer discountValue,
        Boolean active,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        Integer maxUsage,
        Integer minOrderValue
) {
}
