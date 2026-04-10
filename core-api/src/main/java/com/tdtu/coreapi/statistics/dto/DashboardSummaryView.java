package com.tdtu.coreapi.statistics.dto;

public record DashboardSummaryView(
        Long money,
        Integer quantity,
        Integer invoiceNumber,
        Integer userNumber
) {
}
