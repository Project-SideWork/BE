package com.sidework.chat.application.port.in;

public record ChatContent(
        Long receiverId,
        String content
) {
}
