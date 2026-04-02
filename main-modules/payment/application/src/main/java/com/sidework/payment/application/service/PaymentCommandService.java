package com.sidework.payment.application.service;

import com.sidework.payment.application.port.in.PaymentCommandUseCase;
import com.sidework.payment.application.port.out.PaymentOutPort;
import com.sidework.payment.domain.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCommandService implements PaymentCommandUseCase {
    private final PaymentOutPort repo;

    @Override
    public void create(Payment payment) {
        repo.save(payment);
    }
}
