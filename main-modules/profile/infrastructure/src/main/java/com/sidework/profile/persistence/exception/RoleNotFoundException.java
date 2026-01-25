package com.sidework.profile.persistence.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class RoleNotFoundException extends GlobalException {
	public RoleNotFoundException(Long id) {
		super(ErrorStatus.ROLE_NOT_FOUND.withDetail("역할 ID: " + id + "를 찾을 수 없습니다."));
	}
}
