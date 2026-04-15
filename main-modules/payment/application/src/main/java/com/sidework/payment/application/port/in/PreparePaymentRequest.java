package com.sidework.payment.application.port.in;

public record PreparePaymentRequest(
        Integer requestedCredit
) {
}
