package com.sidework.project.application.exception;

import org.springframework.http.HttpStatus;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectNotRecruitingException extends GlobalException {
	public ProjectNotRecruitingException(Long id) {
		super(ErrorStatus.PROJECT_NOT_RECRUITING.withDetail(String.format("해당 프로젝트(id=%d)는 모집 중이 아닙니다.", id)));
	}
}
