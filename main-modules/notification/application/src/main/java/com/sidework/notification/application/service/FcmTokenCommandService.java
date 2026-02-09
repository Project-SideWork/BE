package com.sidework.notification.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.notification.application.port.in.FcmTokenCommandUseCase;
import com.sidework.notification.application.port.out.FcmTokenOutPort;
import com.sidework.notification.domain.FcmToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class FcmTokenCommandService implements FcmTokenCommandUseCase {

	private final FcmTokenOutPort fcmTokenRepository;

	@Override
	public void registerToken(Long userId, String token, boolean pushAgreed) {
		FcmToken fcmToken = FcmToken.create(userId, token, pushAgreed);
		fcmTokenRepository.registerToken(fcmToken);
	}
}
