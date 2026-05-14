package com.sidework.common.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ResourceAlreadyExistException extends GlobalException {
    public ResourceAlreadyExistException(ErrorStatus status) {
        super(status);
    }
}
