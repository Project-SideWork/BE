package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectUserAlreadyReviewedException extends GlobalException {
	public ProjectUserAlreadyReviewedException() {
		super(ErrorStatus.PROJECT_USER_ALREADY_REVIEWED);
	}
}
