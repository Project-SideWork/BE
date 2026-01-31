package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectAlreadyAppliedException extends GlobalException {

	public ProjectAlreadyAppliedException(Long projectId) {
		super(ErrorStatus.PROJECT_ALREADY_APPLIED.withDetail(
			String.format("이미 해당 프로젝트(id=%d)에 지원했습니다.", projectId)));
	}
}
