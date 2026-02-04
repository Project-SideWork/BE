package com.sidework.notification.application.port.out;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseSubscribeOutPort {
	SseEmitter subscribe(Long userId);
}
