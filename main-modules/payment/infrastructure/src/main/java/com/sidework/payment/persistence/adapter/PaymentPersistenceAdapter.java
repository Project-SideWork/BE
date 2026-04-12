package com.sidework.payment.persistence.adapter;

import com.sidework.payment.application.port.out.PaymentOutPort;
import com.sidework.payment.domain.Payment;
import com.sidework.payment.persistence.entity.PaymentEntity;
import com.sidework.payment.persistence.exception.PaymentNotFoundException;
import com.sidework.payment.persistence.mapper.PaymentMapper;
import com.sidework.payment.persistence.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentOutPort {
    private final PaymentJpaRepository repo;
    private final PaymentMapper mapper;

    @Override
    public void save(Payment payment) {
        PaymentEntity entity = mapper.toEntity(payment);
        repo.save(entity);
    }

    @Override
    public Payment findById(String id) {
        PaymentEntity entity = repo.findById(id).orElseThrow(PaymentNotFoundException::new);
        return mapper.toDomain(entity);
    }

    @Override
    public int calculateUsedCredit(String paymentId) {
        return repo.findUsedCreditById(paymentId);
    }
}
