package com.sidework.chat.application.port.out;

import java.time.Instant;

public record ChatUserSummary(Long chatRoomId,
                              String lastMessageContent,
                              Instant lastMessageSentTime,
                              Long unreadCount,
                              Instant createdAt) {
}
