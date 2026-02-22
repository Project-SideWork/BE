package com.sidework.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;

    private Long chatRoomId;

    private Long senderId; // ChatUser의 Id가 아닌 User의 Id

    private String content;

    private Boolean isDeleted;

    private LocalDateTime createdAt;

    public static ChatMessage create(Long chatRoomId, Long senderId, String content) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .isDeleted(false)
                .build();
    }
}
