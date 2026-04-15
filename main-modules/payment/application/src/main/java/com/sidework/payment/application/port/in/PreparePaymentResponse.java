package com.sidework.payment.application.port.in;

public record PreparePaymentResponse(
        String paymentId,
        int approvedCredit,
        int finalAmount
) {
}
