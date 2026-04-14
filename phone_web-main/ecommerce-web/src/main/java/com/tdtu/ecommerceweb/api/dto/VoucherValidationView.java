package com.tdtu.ecommerceweb.api.dto;

public record VoucherValidationView(
        Long voucherId,
        String voucherCode,
        String voucherName,
        String voucherType,
        Integer discountValue,
        Integer minOrderValue,
        Integer discountMoney,
        Integer totalBeforeDiscount,
        Integer totalAfterDiscount
) {
}
