package com.tdtu.coreapi.chat;

import com.tdtu.coreapi.chat.dto.ChatRequest;
import com.tdtu.coreapi.chat.dto.ChatResponse;
import com.tdtu.coreapi.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ApiResponse.success(chatService.chat(request));
    }

    @GetMapping("/{sessionCode}")
    public ApiResponse<ChatResponse> getHistory(@PathVariable String sessionCode) {
        return ApiResponse.success(chatService.getHistory(sessionCode));
    }
}
