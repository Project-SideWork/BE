package com.sidework.profile.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProfileLikeNotFoundException extends GlobalException {
	public ProfileLikeNotFoundException() {
		super(ErrorStatus.PROFILE_LIKE_NOT_FOUND);
	}
}

