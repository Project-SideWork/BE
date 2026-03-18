package com.sidework.profile.application.port.out;

import java.util.List;
import java.util.Map;

import com.sidework.profile.domain.ProfileLike;

public interface ProfileLikeOutPort {
	void like(ProfileLike like);
	void unlike(ProfileLike like);
	boolean isLiked(Long userId, Long profileId);
	Map<Long, Boolean> getLikes(Long userId, List<Long> profileIds);
}

