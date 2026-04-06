package com.sidework.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String paymentId;

    private Long userId;

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

    private LocalDateTime paidAt;

    private LocalDateTime requestedAt;


    public static Payment create(
            String paymentId,
            String transactionId,
            String storeId,
            String orderName,
            Long amount,
            String currency,
            String status,
            String customerName,
            String customerEmail,
            String customerPhone,
            String itemId,
            LocalDateTime paidAt,
            LocalDateTime requestedAt
    ) {
        return Payment.builder()
                .paymentId(paymentId)
                .transactionId(transactionId)
                .storeId(storeId)
                .orderName(orderName)
                .amount(amount)
                .currency(currency)
                .status(status)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .itemId(itemId)
                .paidAt(paidAt)
                .requestedAt(requestedAt)
                .build();
    }

    public void assignUser(Long userId) {
        this.userId = userId;
    }
}
