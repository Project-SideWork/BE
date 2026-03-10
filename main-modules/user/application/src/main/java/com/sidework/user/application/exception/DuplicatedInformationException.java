package com.sidework.user.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class DuplicatedInformationException extends GlobalException {
    public DuplicatedInformationException(ErrorStatus status) {
        super(status);
    }
}
