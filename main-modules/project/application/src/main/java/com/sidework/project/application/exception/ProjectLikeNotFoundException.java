package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectLikeNotFoundException extends GlobalException {
    public ProjectLikeNotFoundException() {
        super(ErrorStatus.PROJECT_LIKE_NOT_FOUND);
    }
}

