package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectNotFinishedException extends GlobalException {
	public ProjectNotFinishedException(Long projectId) {
		super(ErrorStatus.PROJECT_NOT_FINISHED.withDetail("해당 프로젝트 ID:" +  projectId + " 는 종료된 프로젝트가 아닙니다."));
	}
}
