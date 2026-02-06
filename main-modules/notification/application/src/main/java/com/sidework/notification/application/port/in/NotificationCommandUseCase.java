package com.sidework.notification.application.port.in;

import com.sidework.notification.domain.NotificationType;

public interface NotificationCommandUseCase {
	void create(Long userId, NotificationType type, String title, String body);
	void markAsRead(Long notificationId, Long userId);
}
