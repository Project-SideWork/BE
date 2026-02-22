package com.sidework.common.event.sse.port.out;

public interface SseSendOutPort {
    void sendToUser(Long userId, Object data);
    void sendToChatRoom(Long chatRoomId, Object data);
}
