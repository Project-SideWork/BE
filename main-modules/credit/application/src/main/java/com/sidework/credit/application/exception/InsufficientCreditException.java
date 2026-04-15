package com.sidework.credit.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class InsufficientCreditException extends GlobalException {
    public InsufficientCreditException() {
        super(ErrorStatus.CREDIT_NOT_ENOUGH);
    }
}
