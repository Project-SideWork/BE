package com.sidework.notification.application.port.out;

import com.sidework.notification.domain.FcmUserToken;

public interface FcmTokenOutPort {

	void registerToken(FcmUserToken fcmUserToken);
}
