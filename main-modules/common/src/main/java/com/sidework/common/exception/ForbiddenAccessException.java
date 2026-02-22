package com.sidework.common.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ForbiddenAccessException extends GlobalException {
    public ForbiddenAccessException() {
        super(ErrorStatus.FORBIDDEN);
    }
}
