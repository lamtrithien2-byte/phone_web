package com.tdtu.ecommerceweb.api.dto;

import java.util.Date;

public record TrackingRecordView(
        String code,
        String customerName,
        String source,
        String orderStatus,
        String paymentStatus,
        String salesStaffName,
        Integer totalMoney,
        Date createdAt
) {
}
