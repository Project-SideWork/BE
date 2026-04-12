package com.sidework.credit.application.port.out;

import com.sidework.credit.domain.Credit;

import java.util.List;

public interface CreditOutPort {
    void saveAll(List<Credit> domains);
    Long findAmountByUser(Long userId);
    List<Credit> findAvailableCredits(Long userId);
}
