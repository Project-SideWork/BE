package com.sidework.chat.application.port.in;

public record ChatRecord(
        Long chatMessageId,
        Long senderId,
        String content,
        String sentTime
) {
    public static ChatRecord create(Long chatMessageId, Long senderId, String content, String sentTime) {
        return new ChatRecord(chatMessageId, senderId, content, sentTime);
    }
}
