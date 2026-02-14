package com.sidework.chat.application.port.out;

import com.sidework.domain.ChatMessage;

public interface ChatMessageOutPort {
    Long save(ChatMessage chatMessage);
}
