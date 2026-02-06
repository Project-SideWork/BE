package com.sidework.notification.application.adapter;

import java.time.LocalDateTime;

import com.sidework.notification.domain.Notification;
import com.sidework.notification.domain.NotificationType;

public record NotificationResponse(
	Long id,
	Long userId,
	NotificationType type,
	String title,
	String body,
	boolean read
) {
	public static NotificationResponse from(Notification notification) {
		return new NotificationResponse(
			notification.getId(),
			notification.getUserId(),
			notification.getType(),
			notification.getTitle(),
			notification.getBody(),
			notification.isRead()
		);
	}

}
