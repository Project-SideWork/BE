package com.sidework.chat.application.adapter;

import com.sidework.chat.application.port.in.ChatCommandUseCase;
import com.sidework.chat.application.port.in.ChatContent;
import com.sidework.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatCommandUseCase chatCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendNewChat(@RequestBody ChatContent chatContent) {
        chatCommandService.processStartNewChat(chatContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }
}
