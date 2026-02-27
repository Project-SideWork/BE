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
public class ChatRoom {
    private Long id;

    private String lastMessageContent;

    private LocalDateTime lastMessageSentTime;

    public static ChatRoom create(String initMessage) {
        return new ChatRoom(null, initMessage, LocalDateTime.now());
    }
}
