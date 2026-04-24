package com.sidework.profile.application.port.in;

import java.util.List;
import java.util.Map;

public interface ProfileLikeQueryUseCase {
	Map<Long, Boolean> isLikedByProfileIds(Long userId, List<Long> profileIds);

	List<Long> findLikedProfileIds(Long userId);

	boolean isLiked(Long viewerUserId, Long profileId);
}

