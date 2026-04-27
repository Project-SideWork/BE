package com.sidework.common.event;

public record ChatMessageSendEvent(
        Long receiverId, String message
) {
}
