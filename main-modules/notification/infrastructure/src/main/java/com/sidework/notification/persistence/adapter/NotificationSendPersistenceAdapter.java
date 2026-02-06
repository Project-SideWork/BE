package com.sidework.notification.persistence.adapter;

import org.springframework.stereotype.Component;

import com.sidework.notification.application.adapter.NotificationResponse;
import com.sidework.notification.application.port.out.NotificationSendOutPort;
import com.sidework.notification.domain.Notification;
import com.sidework.notification.persistence.sse.SseEmitterManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationSendPersistenceAdapter implements NotificationSendOutPort {

	private final SseEmitterManager sseEmitterManager;

	@Override
	public void send(Long userId, Notification notification) {
		if(notification == null) {
			return;
		}
		NotificationResponse response = NotificationResponse.from(notification);
		sseEmitterManager.sendToUser(userId, response);
	}
}
