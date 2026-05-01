package com.sidework.common.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ResourceUpdateFailedException extends GlobalException {
    public ResourceUpdateFailedException(ErrorStatus errorStatus) {
        super(errorStatus);
    }
}
