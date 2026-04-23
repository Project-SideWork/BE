package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectLikeExistException extends GlobalException {
    public ProjectLikeExistException() {
        super(ErrorStatus.PROJECT_LIKE_ALREADY_EXISTS);
    }
}
