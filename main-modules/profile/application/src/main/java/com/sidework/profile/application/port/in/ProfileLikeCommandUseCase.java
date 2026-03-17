package com.sidework.profile.application.port.in;

public interface ProfileLikeCommandUseCase {
	void like(Long userId, Long profileId);
}
