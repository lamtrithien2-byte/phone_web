package com.tdtu.coreapi.chat.dto;

import java.util.List;

public record ChatResponse(
        String sessionCode,
        String answer,
        List<ChatMessageView> history
) {
}
