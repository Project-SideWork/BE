package com.sidework.common.event;

public record PaymentCompleteEvent(
        Long userId,
        int usedCredit,
        String paymentId
) {
}
