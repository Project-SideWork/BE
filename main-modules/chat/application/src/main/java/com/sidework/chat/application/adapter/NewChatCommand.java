package com.sidework.chat.application.adapter;

public record NewChatCommand(
        Long receiverId,
        String content
) {
}
