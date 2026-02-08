package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProfileNotFoundException extends GlobalException {

	public ProfileNotFoundException(Long profileId) {
		super(ErrorStatus.PROFILE_NOT_FOUND.withDetail("프로필 ID: " + profileId + "의 프로필을 찾을 수 없거나 접근 권한이 없습니다."));
	}
}
