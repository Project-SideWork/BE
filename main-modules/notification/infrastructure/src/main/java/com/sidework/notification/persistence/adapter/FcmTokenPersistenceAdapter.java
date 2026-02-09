package com.sidework.notification.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.notification.application.port.out.FcmTokenOutPort;
import com.sidework.notification.domain.FcmToken;
import com.sidework.notification.persistence.entity.FcmTokenEntity;
import com.sidework.notification.persistence.mapper.FcmTokenMapper;
import com.sidework.notification.persistence.repository.FcmTokenJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FcmTokenPersistenceAdapter implements FcmTokenOutPort {

	private static final int MAX_TOKENS_PER_USER = 5; //사용자 당 기기 제한 5개

	private final FcmTokenJpaRepository fcmTokenJpaRepository;
	private final FcmTokenMapper fcmTokenMapper;

	@Override
	@Transactional
	public void registerToken(FcmToken fcmToken) {
		Long userId = fcmToken.getUserId();
		String token = fcmToken.getToken();
		boolean pushAgreed = fcmToken.isPushAgreed();

		fcmTokenJpaRepository.findByUserIdAndToken(userId, token)
			.ifPresentOrElse(
				existing -> {
					existing.setPushAgreed(pushAgreed);
					fcmTokenJpaRepository.save(existing);
				},
				() -> {
					long count = fcmTokenJpaRepository.countByUserIdForUpdate(userId);
					if (count >= MAX_TOKENS_PER_USER) {
						fcmTokenJpaRepository.deleteOldestTokenByUserId(userId);
					}
					fcmTokenJpaRepository.save(fcmTokenMapper.toEntity(fcmToken));
				}
			);
	}

	@Override
	public List<FcmToken> findTokensByUserId(Long userId) {
		return fcmTokenJpaRepository.findByUserIdAndPushAgreedTrue(userId).stream()
			.map(fcmTokenMapper::toDomain)
			.toList();
	}
}
