package com.tdtu.coreapi.chat;

import com.tdtu.coreapi.chat.dto.ChatRequest;
import com.tdtu.coreapi.chat.dto.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChatService {

    private final ChatProcedureRepository chatProcedureRepository;
    private final ChatAdvisorService chatAdvisorService;

    public ChatService(ChatProcedureRepository chatProcedureRepository, ChatAdvisorService chatAdvisorService) {
        this.chatProcedureRepository = chatProcedureRepository;
        this.chatAdvisorService = chatAdvisorService;
    }

    @Transactional
    public ChatResponse chat(ChatRequest request) {
        String sessionCode = request.sessionCode();
        Long sessionId;
        if (sessionCode == null || sessionCode.isBlank()) {
            sessionCode = "CHAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            sessionId = chatProcedureRepository.createSession(sessionCode, request.customerId(), request.guestName(), "AGENT_WEB");
        } else {
            sessionId = chatProcedureRepository.resolveSessionId(sessionCode);
            if (sessionId == null) {
                sessionId = chatProcedureRepository.createSession(sessionCode, request.customerId(), request.guestName(), "AGENT_WEB");
            }
        }

        chatProcedureRepository.addMessage(sessionId, "USER", request.message(), estimateTokens(request.message()), 0);
        String answer = chatAdvisorService.answer(request.message());
        chatProcedureRepository.addMessage(sessionId, "AI", answer, 0, estimateTokens(answer));

        return new ChatResponse(
                sessionCode,
                answer,
                chatProcedureRepository.getMessages(sessionCode)
        );
    }

    public ChatResponse getHistory(String sessionCode) {
        return new ChatResponse(
                sessionCode,
                null,
                chatProcedureRepository.getMessages(sessionCode)
        );
    }

    private int estimateTokens(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        return Math.max(1, text.trim().split("\\s+").length);
    }
}
