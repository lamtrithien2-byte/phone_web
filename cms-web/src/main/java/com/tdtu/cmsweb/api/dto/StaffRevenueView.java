package com.tdtu.cmsweb.api.dto;

public record StaffRevenueView(
        Long userId,
        String fullName,
        Integer invoiceCount,
        Integer totalQuantity,
        Long totalRevenue
) {
}
