package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class InvalidCommandException extends GlobalException {
    public InvalidCommandException(String detail) {
        super(ErrorStatus.BAD_REQUEST.withDetail(detail));
    }
}

