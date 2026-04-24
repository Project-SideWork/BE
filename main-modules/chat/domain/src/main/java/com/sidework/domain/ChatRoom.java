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

    private Long lastMessageId;

    private Long lastMessageSenderId;

    public static ChatRoom create(String initMessage) {
        return new ChatRoom(null, initMessage, LocalDateTime.now(), null, null);
    }

    public void changeLastMessage(Long lastMessageId, Long lastMessageSenderId) {
        this.lastMessageId = lastMessageId;
        this.lastMessageSenderId = lastMessageSenderId;
    }
}
