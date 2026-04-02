package com.sidework.payment.application.port.in;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CompletePaymentRequest {
    private String paymentId;
}
