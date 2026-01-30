package com.sidework.project.persistence.adapter;

import org.springframework.stereotype.Component;

import com.sidework.profile.application.exception.ProfileNotFoundException;
import com.sidework.project.application.port.out.ProfileValidationOutPort;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileValidationAdapter implements ProfileValidationOutPort {

	private final ProfileQueryUseCase profileQueryUseCase;

	@Override
	public void validateProfileExistsAndOwnedByUser(Long profileId, Long userId) {
		if (!profileQueryUseCase.existsByIdAndUserId(profileId,userId)) {
			throw new ProfileNotFoundException(profileId);
		}
	}
}
