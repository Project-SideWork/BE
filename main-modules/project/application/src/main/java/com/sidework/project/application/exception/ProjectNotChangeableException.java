package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;

import static com.sidework.common.response.status.ErrorStatus.PROJECT_CANNOT_UPDATE;

public class ProjectNotChangeableException extends GlobalException {
    public ProjectNotChangeableException(Long id) {
        super(PROJECT_CANNOT_UPDATE.withDetail(String.format(
                "%s (value=%s)",
                PROJECT_CANNOT_UPDATE.getMessage(),
                id
        )));
    }
}
