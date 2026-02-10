package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectOwnerNotFoundException extends GlobalException {
  public ProjectOwnerNotFoundException(Long id) {
    super(ErrorStatus.PROJECT_OWNER_NOT_FOUND.withDetail(String.format("해당 프로젝트(id=%d)의 소유자가 아닙니다.", id)));
  }
}
