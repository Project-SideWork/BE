package com.sidework.notification.persistence.adapter;

import org.springframework.stereotype.Component;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.sidework.notification.application.port.out.FcmPushOutPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushAdapter implements FcmPushOutPort {

	private final FirebaseMessaging firebaseMessaging;

	@Override
	public void sendToToken(String token, String title, String body) {
		Message message = Message.builder()
			.setToken(token)
			.putData("title", title)
			.putData("body", body)
			.build();

		try {
			firebaseMessaging.send(message);
			log.info("FCM push sent successfully. token={}", token.substring(0, Math.min(20, token.length())) + "...");
		} catch (FirebaseMessagingException e) {
			log.warn("FCM push failed. token={}, error={}", token.substring(0, Math.min(20, token.length())) + "...", e.getMessage());
			throw new RuntimeException("FCM push failed: " + e.getMessage(), e);
		}
	}
}
