package com.tdtu.ecommerceweb.api.dto;

import java.util.List;

public record ChatResponse(
        String sessionCode,
        String answer,
        List<ChatMessageView> history
) {
}
