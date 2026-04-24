package com.sidework.profile.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProfileLikeExistException extends GlobalException {
	public ProfileLikeExistException() {
		super(ErrorStatus.PROFILE_LIKE_ALREADY_EXISTS);
	}
}

