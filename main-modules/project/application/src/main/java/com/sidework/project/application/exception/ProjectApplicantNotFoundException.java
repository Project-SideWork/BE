package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectApplicantNotFoundException extends GlobalException {
  public ProjectApplicantNotFoundException(Long id) {
    super(ErrorStatus.PROJECT_APPLICANT_NOT_FOUND.withDetail(String.format("해당 프로젝트(id=%d)의 신청자가 아닙니다.", id)));
  }
}
