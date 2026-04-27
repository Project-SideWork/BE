package com.sidework.profile.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.profile.application.exception.ProfileLikeExistException;
import com.sidework.profile.application.exception.ProfileLikeNotFoundException;
import com.sidework.profile.application.exception.ProfileNotFoundException;
import com.sidework.profile.application.port.in.ProfileLikeCommandUseCase;
import com.sidework.profile.application.port.out.ProfileLikeOutPort;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.domain.ProfileLike;
import com.sidework.user.application.port.in.UserQueryUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProfileLikeCommandService implements ProfileLikeCommandUseCase {

	private final ProfileLikeOutPort profileLikeRepository;
	private final ProfileOutPort profileRepository;
	private final UserQueryUseCase userRepository;

	@Override
	public void like(Long userId, Long profileId) {
		userRepository.validateExists(userId);
		if (!profileRepository.existsById(profileId)) {
			throw new ProfileNotFoundException(profileId);
		}

		if (profileLikeRepository.isLiked(userId, profileId)) {
			throw new ProfileLikeExistException();
		}

		ProfileLike like = ProfileLike.create(userId, profileId);
		profileLikeRepository.like(like);
	}

	@Override
	public void delete(Long userId, Long profileId) {
		userRepository.validateExists(userId);
		if (!profileRepository.existsById(profileId)) {
			throw new ProfileNotFoundException(profileId);
		}

		if (profileLikeRepository.unlike(userId, profileId) == 0) {
			throw new ProfileLikeNotFoundException();
		}
	}
}
