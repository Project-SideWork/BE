package com.sidework.project.application.port.out;

public interface ProfileValidationOutPort {

	void validateProfileExistsAndOwnedByUser(Long profileId, Long userId);
}
