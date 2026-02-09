package com.sidework.notification.application.port.in;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseSubscribeUseCase {

	SseEmitter subscribe(Long userId);
}
