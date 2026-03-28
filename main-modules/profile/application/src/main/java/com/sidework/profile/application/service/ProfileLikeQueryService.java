package com.sidework.profile.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.profile.application.port.in.ProfileLikeQueryUseCase;
import com.sidework.profile.application.port.out.ProfileLikeOutPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileLikeQueryService implements ProfileLikeQueryUseCase {

	private final ProfileLikeOutPort profileLikeOutPort;

	@Override
	public Map<Long, Boolean> isLikedByProfileIds(Long userId, List<Long> profileIds) {
		return profileLikeOutPort.getLikes(userId, profileIds);
	}

	@Override
	public List<Long> findLikedProfileIds(Long userId) {
		return profileLikeOutPort.findLikedProfileIds(userId);
	}
}

