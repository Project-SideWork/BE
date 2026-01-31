package com.sidework.profile.application.port.in;

import com.sidework.profile.application.adapter.UserProfileResponse;

public interface ProfileQueryUseCase {
	UserProfileResponse getProfileByUserId(Long userId);
	boolean existsByIdAndUserId(Long profileId,Long userId);
}
