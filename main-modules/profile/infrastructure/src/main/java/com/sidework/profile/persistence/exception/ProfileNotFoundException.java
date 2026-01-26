package com.sidework.profile.persistence.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProfileNotFoundException extends GlobalException {
	public ProfileNotFoundException(Long userId) {
		super(ErrorStatus.PROFILE_NOT_FOUND.withDetail("사용자 ID: " + userId + "의 프로필을 찾을 수 없습니다."));
	}
}
