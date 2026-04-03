package com.sidework.payment.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity extends BaseEntity {
    @Id
    private String paymentId;

    private String transactionId;

    private String storeId;

    private String orderName;

    private Long amount;

    private String currency;

    private String status;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String itemId;

    private Instant paidAt;

    private Instant requestedAt;
}
