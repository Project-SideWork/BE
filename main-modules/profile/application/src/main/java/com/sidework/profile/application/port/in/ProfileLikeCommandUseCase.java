package com.sidework.profile.application.port.in;

public interface ProfileLikeCommandUseCase {
	void like(Long userId, Long profileId);
	void delete(Long userId, Long profileId);
}
