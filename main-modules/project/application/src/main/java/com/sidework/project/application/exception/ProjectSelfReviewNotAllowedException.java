package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectSelfReviewNotAllowedException extends GlobalException {
	public ProjectSelfReviewNotAllowedException() {
		super(ErrorStatus.PROJECT_SELF_REVIEW_NOT_ALLOWED);
	}
}
