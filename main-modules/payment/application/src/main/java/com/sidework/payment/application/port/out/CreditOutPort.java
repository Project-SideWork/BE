package com.sidework.payment.application.port.out;

public interface CreditOutPort {
    Long findAmountByUser(Long userId);
}
