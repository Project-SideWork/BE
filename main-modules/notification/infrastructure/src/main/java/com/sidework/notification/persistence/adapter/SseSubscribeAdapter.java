package com.sidework.notification.persistence.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sidework.notification.application.port.out.SseSubscribeOutPort;
import com.sidework.notification.persistence.sse.SseEmitterManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SseSubscribeAdapter implements SseSubscribeOutPort {

	private final SseEmitterManager sseEmitterManager;

	@Override
	public SseEmitter subscribe(Long userId) {
		return sseEmitterManager.subscribe(userId);
	}
}
