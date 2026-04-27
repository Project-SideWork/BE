package com.sidework.chat.application.adapter;

public record ChatRoomRecord(
        Long chatRoomId,
        String latestMessage,
        String latestSentTime,
        Long unreadCount
){
    public static ChatRoomRecord create(Long chatRoomId, String latestMessage, String latestSentTime, Long unreadCount) {
        return new ChatRoomRecord(chatRoomId, latestMessage, latestSentTime, unreadCount);
    }
}
