package com.sidework.chat.application.port.out;

import com.sidework.domain.ChatMessage;

import java.time.Instant;

public interface ChatMessageOutPort {
    Long save(ChatMessage chatMessage);
    ChatMessagePage findByChatRoomIdAndIdGreaterThan(
            Long chatRoomId, Instant cursorCreatedAt, Long cursorId, int size
    );
}
