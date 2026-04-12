package com.sidework.payment.application.port.in;

import com.sidework.payment.domain.Payment;

import java.util.concurrent.CompletableFuture;

public interface PaymentCommandUseCase {
    CompletableFuture<Payment> syncPayment(String paymentId);
    void processAfterPaymentCompleted(Long userId, String paymentId);
}
