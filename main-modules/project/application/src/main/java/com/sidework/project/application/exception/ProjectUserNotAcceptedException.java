package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.BaseStatusCode;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectUserNotAcceptedException extends GlobalException {
  public ProjectUserNotAcceptedException(Long projectId) {
    super(ErrorStatus.PROJECT_NOT_ACCEPTED.withDetail("해당 프로젝트 ID: " + projectId + " 의 승인된 멤버가 아닙니다."));
  }
}
