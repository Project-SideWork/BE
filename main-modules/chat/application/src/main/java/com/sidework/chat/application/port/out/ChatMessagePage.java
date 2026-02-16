package com.sidework.chat.application.port.out;

import com.sidework.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessagePage(
        List<ChatMessage> items,
        Boolean hasNext,
        LocalDateTime nextCursorCreatedAt,
        Long nextCursorId
) {
}
