package com.sidework.profile.persistence.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sidework.profile.application.port.out.ProfileLikeOutPort;
import com.sidework.profile.domain.ProfileLike;
import com.sidework.profile.persistence.mapper.ProfileLikeMapper;
import com.sidework.profile.persistence.repository.ProfileLikeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileLikePersistenceAdapter implements ProfileLikeOutPort {

	private final ProfileLikeRepository profileLikeRepository;
	private final ProfileLikeMapper profileLikeMapper;

	@Override
	public void like(ProfileLike like) {
		profileLikeRepository.save(profileLikeMapper.toEntity(like));
	}

	@Override
	public void unlike(ProfileLike like) {
		profileLikeRepository.deleteByUserIdAndProfileId(like.getUserId(), like.getProfileId());
	}

	@Override
	public boolean isLiked(Long userId, Long profileId) {
		return profileLikeRepository.existsByUserIdAndProfileId(userId, profileId);
	}

	@Override
	public Map<Long, Boolean> getLikes(Long userId, List<Long> profileIds) {
		if (profileIds == null || profileIds.isEmpty()) {
			return Map.of();
		}
		Set<Long> profileIdSet = new HashSet<>(
			profileLikeRepository.findProfileIdsByUserIdAndProfileIdIn(userId, profileIds)
		);

		return profileIds.stream()
			.collect(Collectors.toMap(id -> id, profileIdSet::contains));
	}

	@Override
	public List<Long> findLikedProfileIds(Long userId) {
		return profileLikeRepository.findProfileIdsByUserId(userId);
	}
}

