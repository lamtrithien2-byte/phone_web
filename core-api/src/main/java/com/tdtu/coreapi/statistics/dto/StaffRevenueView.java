package com.tdtu.coreapi.statistics.dto;

public record StaffRevenueView(
        Long userId,
        String fullName,
        Integer invoiceCount,
        Integer totalQuantity,
        Long totalRevenue
) {
}
