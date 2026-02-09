package com.sidework.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmToken {

	private Long id;
	private Long userId;
	private String token;
	private boolean pushAgreed;

	public static FcmToken create(Long userId, String token, boolean pushAgreed) {
		return FcmToken.builder()
			.userId(userId)
			.token(token)
			.pushAgreed(pushAgreed)
			.build();
	}
}
