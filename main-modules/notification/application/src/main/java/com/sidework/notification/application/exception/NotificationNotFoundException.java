package com.sidework.notification.application.exception;

import org.springframework.http.HttpStatus;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class NotificationNotFoundException extends GlobalException {
	public NotificationNotFoundException(Long notificationId) {
		super(ErrorStatus.NOTIFICATION_NOT_FOUND.withDetail("알림 ID: " + notificationId + "의 알림을 찾을 수 없습니다."));
	}
}
