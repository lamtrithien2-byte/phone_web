package com.tdtu.ecommerceweb.api.dto;

public record ChatRequest(
        String sessionCode,
        Long customerId,
        String guestName,
        String message
) {
}
