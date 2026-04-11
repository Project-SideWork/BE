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

    private String paymentId;

    private Integer amount;

    private CreditType type;

    private LocalDate expiredAt;

    public static Credit create(
            Long userId,
            String paymentId,
            Integer amount,
            CreditType type,
            LocalDate expiredAt
    ) {
        validate(userId, amount, type);

        return Credit.builder()
                .userId(userId)
                .paymentId(paymentId)
                .amount(amount)
                .type(type)
                .expiredAt(expiredAt)
                .build();
    }

    private static void validate(Long userId, Integer amount, CreditType type) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("amount는 0보다 커야 합니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type은 필수입니다.");
        }
    }
}