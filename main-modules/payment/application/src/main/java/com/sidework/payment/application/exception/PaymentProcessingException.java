package com.sidework.payment.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class PaymentProcessingException extends GlobalException {
    public PaymentProcessingException() {
        super(ErrorStatus.PAYMENT_AFTER_PROCESS_FAIL);
    }
}
