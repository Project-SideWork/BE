package com.sidework.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {
    private Long id;

    private Long userId;

    private Long amount;

    private CreditType type;

    private String description;

    private Long referenceId;

    private Instant expiredAt;
}