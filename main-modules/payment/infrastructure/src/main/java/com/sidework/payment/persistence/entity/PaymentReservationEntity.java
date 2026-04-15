package com.sidework.payment.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "payment_reservations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentReservationEntity extends BaseEntity {
    @Id
    private String paymentId;

    private Long userId;

    private Integer approvedCredit;

    private String status;
}
