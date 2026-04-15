package com.sidework.payment.persistence.repository;

import com.sidework.payment.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, String> {
    @Query(
            """
            SELECT p.originalAmount - p.amount FROM PaymentEntity p
            WHERE p.paymentId = :paymentId
            """
    )
    int findUsedCreditById(@Param("paymentId") String paymentId);
}
