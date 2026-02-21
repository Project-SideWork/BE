package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectApplyAlreadyProcessedException extends GlobalException {
	public ProjectApplyAlreadyProcessedException(Long projectId, Long applicantUserId) {
		super(ErrorStatus.PROJECT_APPLY_ALREADY_PROCESSED.withDetail(
			String.format("해당 프로젝트(id=%d)의 지원자(userId=%d)는 이미 처리되었습니다.", projectId, applicantUserId)));
	}
}
