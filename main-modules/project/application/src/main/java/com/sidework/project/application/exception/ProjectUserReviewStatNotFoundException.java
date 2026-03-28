package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectUserReviewStatNotFoundException extends GlobalException {
	public ProjectUserReviewStatNotFoundException(Long userId) {
		super(ErrorStatus.PROJECT_USER_REVIEW_STAT_NOT_FOUND.withDetail("해당 유저 ID: " + userId + "의 프로젝트 평점을 찾을 수 없습니다."));
	}
}
