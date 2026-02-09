package com.sidework.notification.application.port.out;

import java.util.List;

import com.sidework.notification.domain.FcmUserToken;

public interface FcmTokenOutPort {

	void registerToken(FcmUserToken fcmUserToken);

	List<FcmUserToken> findTokensByUserId(Long userId);
}
