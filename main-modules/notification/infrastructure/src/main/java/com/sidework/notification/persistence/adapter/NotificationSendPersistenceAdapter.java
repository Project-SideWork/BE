package com.sidework.notification.persistence.adapter;

import com.sidework.common.event.sse.component.UserSseEmitter;
import org.springframework.stereotype.Component;

import com.sidework.notification.application.adapter.NotificationResponse;
import com.sidework.notification.application.port.out.NotificationSendOutPort;
import com.sidework.notification.domain.Notification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationSendPersistenceAdapter implements NotificationSendOutPort {

	private final UserSseEmitter sseEmitterManager;

	@Override
	public void send(Long userId, Notification notification) {
		if(notification == null) {
			return;
		}
		NotificationResponse response = NotificationResponse.from(notification);
		sseEmitterManager.sendTo(userId, response);
	}
}
