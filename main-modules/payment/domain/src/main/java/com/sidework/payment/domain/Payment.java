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

    private Integer originalAmount; // 원래 가격

    private Integer amount; // 실제 결제 금액

    private String currency;

    private String status;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String itemId;

    private boolean processed;

    private LocalDateTime paidAt;

    private LocalDateTime requestedAt;


    public static Payment create(
            String paymentId,
            String transactionId,
            String storeId,
            String orderName,
            Integer originalAmount,
            Integer amount,
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
                .originalAmount(originalAmount)
                .amount(amount)
                .currency(currency)
                .status(status)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .itemId(itemId)
                .processed(false)
                .paidAt(paidAt)
                .requestedAt(requestedAt)
                .build();
    }

    public void assignUser(Long userId) {
        this.userId = userId;
    }

    public boolean isAlreadyProcessed() {
        return this.processed;
    }

    public void process() {
        if (this.processed) {
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }
        this.processed = true;
    }
}
