package com.sidework.payment.application.port.out;

import com.sidework.payment.domain.Payment;

public interface PaymentOutPort {
    void save(Payment payment);
}
