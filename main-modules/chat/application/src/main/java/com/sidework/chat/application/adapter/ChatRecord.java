package com.sidework.chat.application.adapter;

public record ChatRecord(
        Long chatId,
        String content,
        String sentTime
) {
    public static ChatRecord create(Long chatId, String content, String sentTime) {
        return new ChatRecord(chatId, content, sentTime);
    }
}
