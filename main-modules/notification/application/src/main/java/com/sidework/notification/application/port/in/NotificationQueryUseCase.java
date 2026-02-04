package com.sidework.notification.application.port.in;

import java.util.List;

import com.sidework.notification.application.adapter.NotificationResponse;

public interface NotificationQueryUseCase {
	List<NotificationResponse> getByUserId(Long userId);

}
