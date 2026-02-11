package com.sidework.notification.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.sidework.notification.application.port.in.SseSubscribeUseCase;
import com.sidework.notification.application.port.out.SseSubscribeOutPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SseSubscribeService implements SseSubscribeUseCase {

	private final SseSubscribeOutPort sseSubscribeOutPort;

	@Override
	public SseEmitter subscribe(Long userId) {
		return sseSubscribeOutPort.subscribe(userId);
	}
}
