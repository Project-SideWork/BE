package com.sidework.profile.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class RoleNotFoundException extends GlobalException {
	public RoleNotFoundException() {
		super(ErrorStatus.ROLE_NOT_FOUND);
	}
}
