package com.sidework.project.persistence.adapter;

import org.springframework.stereotype.Component;

import com.sidework.project.application.port.out.ProfileQueryOutPort;
import com.sidework.profile.persistence.repository.ProfileJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileQueryAdapter implements ProfileQueryOutPort {

	private final ProfileJpaRepository profileJpaRepository;

	@Override
	public boolean existsByIdAndUserId(Long profileId, Long userId) {
		return profileJpaRepository.existsByIdAndUserId(profileId, userId);
	}
}
