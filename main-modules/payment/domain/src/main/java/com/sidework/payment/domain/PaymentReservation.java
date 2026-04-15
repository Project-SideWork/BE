package com.sidework.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReservation {

    private String paymentId;

    private Long userId;

    private Integer approvedCredit;

    private String status;


    public static PaymentReservation create(String paymentId, Long userId, Integer approvedCredit, String status){
        return PaymentReservation.builder()
                .paymentId(paymentId)
                .userId(userId)
                .approvedCredit(approvedCredit)
                .status(status)
                .build();
    }

    public void paid() {
        this.status = "PAID";
    }
}