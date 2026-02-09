package com.sidework.notification.application.port.out;

import java.util.List;

import com.sidework.notification.domain.FcmToken;

public interface FcmTokenOutPort {

	void registerToken(FcmToken fcmToken);

	List<FcmToken> findTokensByUserId(Long userId);
}
