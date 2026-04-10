package com.tdtu.coreapi.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        String sessionCode,
        Long customerId,
        String guestName,
        @NotBlank String message
) {
}
