package com.sidework.project.persistence.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectNotFoundException extends GlobalException {
    public ProjectNotFoundException(Long id) {
        super(ErrorStatus.PROJECT_NOT_FOUND.withDetail(String.format("프로젝트(id=%d)를 찾을 수 없습니다.", id)));
    }
}
