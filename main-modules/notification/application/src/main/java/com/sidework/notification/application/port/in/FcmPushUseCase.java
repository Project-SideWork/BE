package com.sidework.notification.application.port.in;

public interface FcmPushUseCase {

	void sendToUser(Long userId, String title, String body);
}
