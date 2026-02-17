package com.sidework.common.event.sse.port.in;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseSubscribeUseCase {
	SseEmitter subscribeUser();
	SseEmitter subscribeChat(Long chatRoomId);
}
