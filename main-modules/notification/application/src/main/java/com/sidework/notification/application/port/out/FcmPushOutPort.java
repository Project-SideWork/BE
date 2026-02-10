package com.sidework.notification.application.port.out;

public interface FcmPushOutPort {

	void sendToToken(String token, String title, String body);
}
