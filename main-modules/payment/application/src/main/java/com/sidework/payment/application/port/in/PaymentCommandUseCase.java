package com.sidework.payment.application.port.in;

import com.sidework.payment.domain.Payment;

public interface PaymentCommandUseCase {
    void create(Payment payment);
}
