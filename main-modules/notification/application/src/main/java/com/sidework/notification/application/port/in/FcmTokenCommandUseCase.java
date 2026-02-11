package com.sidework.notification.application.port.in;

public interface FcmTokenCommandUseCase {

	void registerToken(Long userId, String token, boolean pushAgreed);
}
