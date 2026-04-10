package com.tdtu.ecommerceweb.api;

import com.tdtu.ecommerceweb.api.dto.ChatRequest;
import com.tdtu.ecommerceweb.api.dto.ChatResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ChatApiClient {

    private static final ParameterizedTypeReference<ApiResponse<ChatResponse>> CHAT_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public ChatApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ChatResponse chat(ChatRequest request) {
        ApiResponse<ChatResponse> response = restClient.post()
                .uri("/api/chat")
                .body(request)
                .retrieve()
                .body(CHAT_RESPONSE);
        return response != null ? response.data() : null;
    }

    public ChatResponse getHistory(String sessionCode) {
        ApiResponse<ChatResponse> response = restClient.get()
                .uri("/api/chat/{sessionCode}", sessionCode)
                .retrieve()
                .body(CHAT_RESPONSE);
        return response != null ? response.data() : null;
    }
}
