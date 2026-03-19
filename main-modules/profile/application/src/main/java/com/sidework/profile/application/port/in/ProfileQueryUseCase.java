package com.sidework.profile.application.port.in;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.sidework.common.response.PageResponse;
import com.sidework.profile.application.adapter.UserProfileListResponse;
import com.sidework.profile.application.adapter.UserProfileResponse;

public interface ProfileQueryUseCase {
	UserProfileResponse getProfileByUserId(Long userId);
	boolean existsByIdAndUserId(Long profileId,Long userId);
	PageResponse<List<UserProfileListResponse>> getUserProfileList(Long viewerUserId, List<Long> skillIds, Pageable pageable);
	PageResponse<List<UserProfileListResponse>> getLikedUserProfileList(Long viewerUserId, List<Long> skillIds, Pageable pageable);
}
