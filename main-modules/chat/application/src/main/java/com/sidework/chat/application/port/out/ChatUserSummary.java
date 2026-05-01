package com.sidework.chat.application.port.out;

import java.time.LocalDateTime;

public record ChatUserSummary(Long chatRoomId,
                              String lastMessageContent,
                              LocalDateTime lastMessageSentTime,
                              Long unreadCount,
                              LocalDateTime createdAt) {
}
