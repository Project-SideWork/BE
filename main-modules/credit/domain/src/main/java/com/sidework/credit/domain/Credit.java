package com.sidework.credit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Credit {
    private Long id;

    private Long userId;

    private Integer amount;

    private Integer remainingAmount;

    private LocalDate expiresAt;

    public static Credit create(
            Long userId,
            Integer amount,
            LocalDate expiresAt
    ) {
        validate(userId, amount);

        return Credit.builder()
                .userId(userId)
                .amount(amount)
                .remainingAmount(amount)
                .expiresAt(expiresAt)
                .build();
    }

    public void spend(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("차감 금액은 0보다 커야 합니다.");
        }
        if (remainingAmount == null || remainingAmount < amount) {
            throw new IllegalArgumentException("보유 크레딧이 부족합니다.");
        }
        this.remainingAmount -= amount;
    }

    private static void validate(Long userId, Integer amount) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount는 0보다 커야 합니다.");
        }
    }
}