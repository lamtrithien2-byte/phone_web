package com.tdtu.cmsweb.api.dto;

public record DashboardSummaryView(
        Long money,
        Integer quantity,
        Integer invoiceNumber,
        Integer userNumber
) {
}
