package com.sidework.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {
    private Long id;

    private Long chatRoomId;

    private Long userId;

    private Long lastReadChatId;

    public static ChatUser create(Long chatRoomId, Long userId, Long chatId) {
        return ChatUser.builder()
                .chatRoomId(chatRoomId)
                .userId(userId)
                .lastReadChatId(chatId)
                .build();
    }
}
