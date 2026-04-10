package com.tdtu.coreapi.chat.dto;

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
