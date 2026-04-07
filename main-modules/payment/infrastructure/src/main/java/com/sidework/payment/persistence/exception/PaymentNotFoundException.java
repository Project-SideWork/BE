package com.sidework.payment.persistence.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class PaymentNotFoundException extends GlobalException {
    public PaymentNotFoundException() {
        super(ErrorStatus.PAYMENT_NOT_FOUND);
    }
}
