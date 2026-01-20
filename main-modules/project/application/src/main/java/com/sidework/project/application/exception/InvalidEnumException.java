package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class InvalidEnumException extends GlobalException {
    public InvalidEnumException(String value) {
        super(
                ErrorStatus.INVALID_ENUM
                        .withDetail(String.format(
                                "%s (value=%s)",
                                ErrorStatus.INVALID_ENUM.getMessage(),
                                value
                        ))
        );
    }
}
