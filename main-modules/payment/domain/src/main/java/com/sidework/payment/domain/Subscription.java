package com.sidework.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    private Long id;

    private Long userId;

    private SubscriptionStatus subscriptionStatus;

    private LocalDate startsAt;

    private LocalDate expiresAt;

    private LocalDate cancelledAt;
}