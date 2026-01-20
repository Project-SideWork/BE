package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;

import static com.sidework.common.response.status.ErrorStatus.PROJECT_CANNOT_DELETE;

public class ProjectDeleteAuthorityException extends GlobalException {
    public ProjectDeleteAuthorityException(Long id) {
        super(PROJECT_CANNOT_DELETE.withDetail(String.format(
                "%s (value=%s)",
                PROJECT_CANNOT_DELETE.getMessage(),
                id
        )));
    }
}
