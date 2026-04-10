package com.sidework.payment.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import com.sidework.payment.domain.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "subscription")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;

    private Instant startsAt;

    private Instant expiresAt;

    private Instant cancelledAt;
}
