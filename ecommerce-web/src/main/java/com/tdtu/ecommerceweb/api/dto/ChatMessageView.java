package com.tdtu.ecommerceweb.api.dto;

import java.util.Date;

public record ChatMessageView(
        String sessionCode,
        String senderType,
        String messageText,
        Integer promptTokens,
        Integer completionTokens,
        Date createdAt
) {
}
