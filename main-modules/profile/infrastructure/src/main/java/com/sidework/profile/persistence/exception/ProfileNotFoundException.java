package com.sidework.profile.persistence.exception;

import com.sidework.common.response.exception.GlobalException;

import com.sidework.common.response.status.ErrorStatus;

public class ProfileNotFoundException extends GlobalException {
	public ProfileNotFoundException() {
		super(ErrorStatus.PROFILE_NOT_FOUND);
	}
}
