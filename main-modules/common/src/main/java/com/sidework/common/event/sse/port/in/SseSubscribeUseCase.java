package com.sidework.common.event.sse.port.in;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseSubscribeUseCase {
	SseEmitter subscribeUser(Long userId);
	SseEmitter subscribeChat(Long userId, Long chatRoomId);
}
