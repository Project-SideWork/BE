package com.sidework.profile.persistence.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class SchoolNotFoundException extends GlobalException {
	public SchoolNotFoundException(Long id) {
		super(ErrorStatus.SCHOOL_NOT_FOUND.withDetail("학교 ID: " + id + "를 찾을 수 없습니다."));
	}
}

