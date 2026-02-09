package com.sidework.notification.application.service;

import org.springframework.stereotype.Service;

import com.sidework.notification.application.port.in.FcmPushUseCase;
import com.sidework.notification.application.port.out.FcmPushOutPort;
import com.sidework.notification.application.port.out.FcmTokenOutPort;
import com.sidework.notification.domain.FcmUserToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmPushService implements FcmPushUseCase {

	private final FcmTokenOutPort fcmTokenRepository;
	private final FcmPushOutPort fcmSendRepository;

	@Override
	public void sendToUser(Long userId, String title, String body) {
		var tokens = fcmTokenRepository.findTokensByUserId(userId);
		if (tokens.isEmpty()) {
			log.info("No FCM tokens for userId={}, skip push", userId);
			return;
		}
		for (FcmUserToken fcmToken : tokens) {
			try {
				fcmSendRepository.sendToToken(fcmToken.getToken(), title, body);
			} catch (Exception e) {
				log.warn("FCM send failed for userId={}, tokenId={}", userId, fcmToken.getId(), e);
			}
		}
	}
}
