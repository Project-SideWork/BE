package com.sidework.common.event.sse.port.out;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseSubscribeOutPort {
    SseEmitter subscribeUser(Long userId);
    SseEmitter subscribeChatRoom(Long chatRoomId);
}
