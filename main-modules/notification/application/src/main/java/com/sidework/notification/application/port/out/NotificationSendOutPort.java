package com.sidework.notification.application.port.out;

import com.sidework.notification.domain.Notification;

public interface NotificationSendOutPort {
	void send(Long userId, Notification notification);
}
