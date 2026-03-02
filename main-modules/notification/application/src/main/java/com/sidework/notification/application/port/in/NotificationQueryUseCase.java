package com.sidework.notification.application.port.in;


import com.sidework.common.response.CursorResponse;
import com.sidework.notification.application.adapter.NotificationResponse;

public interface NotificationQueryUseCase {
	CursorResponse<NotificationResponse> getByUserId(Long userId, String cursor, int size);

}
