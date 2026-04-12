package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectRetrospectiveAlreadyExistsException extends GlobalException {
	public ProjectRetrospectiveAlreadyExistsException() {
		super(ErrorStatus.PROJECT_RETROSPECTIVE_ALREADY_EXISTS);
	}
}
