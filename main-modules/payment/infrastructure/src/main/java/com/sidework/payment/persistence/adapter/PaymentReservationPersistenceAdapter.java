package com.sidework.payment.persistence.adapter;

import com.sidework.payment.application.port.out.PaymentReservationOutPort;
import com.sidework.payment.domain.PaymentReservation;
import com.sidework.payment.persistence.entity.PaymentReservationEntity;
import com.sidework.payment.persistence.exception.PaymentNotFoundException;
import com.sidework.payment.persistence.mapper.PaymentReservationMapper;
import com.sidework.payment.persistence.repository.PaymentReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentReservationPersistenceAdapter implements PaymentReservationOutPort {
    private final PaymentReservationRepository repo;
    private final PaymentReservationMapper mapper;

    @Override
    public void save(PaymentReservation domain) {
        repo.save(mapper.toEntity(domain));
    }

    @Override
    public PaymentReservation findById(String paymentId) {
        PaymentReservationEntity entity = repo.findById(paymentId).orElseThrow(PaymentNotFoundException::new);
        return mapper.toDomain(entity);
    }
}
