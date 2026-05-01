package com.sidework.common.event.sse.port.out;


public interface SseSendOutPort {
    void sendToUser(Long userId, String notificationType);
    void sendToChatRoom(Long chatRoomId, ChatMessageData data);
}
