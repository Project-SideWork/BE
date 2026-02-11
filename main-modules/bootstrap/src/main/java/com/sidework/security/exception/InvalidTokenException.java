package com.sidework.security.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class InvalidTokenException extends GlobalException {
    public InvalidTokenException() {
        super(ErrorStatus.INVALID_JWT);
    }
}
