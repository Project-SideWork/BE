package com.sidework.project.application.port.out;

public interface ProfileQueryOutPort {

	boolean existsByIdAndUserId(Long profileId, Long userId);
}
