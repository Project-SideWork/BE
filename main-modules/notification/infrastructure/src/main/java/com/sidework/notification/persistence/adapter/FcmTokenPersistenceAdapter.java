package com.sidework.notification.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.notification.application.port.out.FcmTokenOutPort;
import com.sidework.notification.domain.FcmUserToken;
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
	public void registerToken(FcmUserToken fcmUserToken) {
		Long userId = fcmUserToken.getUserId();
		String token = fcmUserToken.getToken();
		boolean pushAgreed = fcmUserToken.isPushAgreed();

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
					fcmTokenJpaRepository.save(fcmTokenMapper.toEntity(fcmUserToken));
				}
			);
	}

	@Override
	public List<FcmUserToken> findTokensByUserId(Long userId) {
		return fcmTokenJpaRepository.findByUserIdAndPushAgreedTrue(userId).stream()
			.map(fcmTokenMapper::toDomain)
			.toList();
	}
}
