package com.sidework.notification.application.port.out;

import java.util.List;

import com.sidework.notification.domain.Notification;

public interface NotificationOutPort {
	void save(Notification notification);
	void update(Notification notification);
	List<Notification> findByUserId(Long userId);
	Notification findById(Long id);
}
