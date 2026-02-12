package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectUserNotFoundException extends GlobalException {
  public ProjectUserNotFoundException(Long id) {
    super(ErrorStatus.PROJECT_USER_NOT_FOUND.withDetail(String.format("해당 프로젝트(id=%d)의 유저/소유자가 아닙니다.", id)));
  }
}
