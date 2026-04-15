package com.sidework.payment.persistence.repository;

import com.sidework.payment.persistence.entity.PaymentReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentReservationRepository extends JpaRepository<PaymentReservationEntity, String> {
}
