package com.sidework.payment.application.port.in;

import com.sidework.payment.domain.Payment;

import java.util.concurrent.CompletableFuture;

public interface PaymentCommandUseCase {
    void create(Payment payment);
    void assignUser(Long userId, String paymentId);
    CompletableFuture<Payment> syncPayment(String paymentId);
}
