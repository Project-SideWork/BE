package com.sidework.payment.application.port.out;

import com.sidework.payment.domain.PaymentReservation;

public interface PaymentReservationOutPort {
    void save(PaymentReservation domain);
    PaymentReservation findById(String paymentId);
}
