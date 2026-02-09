package com.sidework.security.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class UnauthenticatedException extends GlobalException {
    public UnauthenticatedException() {
        super(ErrorStatus.UNAUTHORIZED);
    }
}
