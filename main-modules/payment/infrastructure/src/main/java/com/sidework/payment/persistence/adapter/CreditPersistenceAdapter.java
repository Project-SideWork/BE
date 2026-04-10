package com.sidework.payment.persistence.adapter;

import com.sidework.payment.application.port.out.CreditOutPort;
import com.sidework.payment.persistence.mapper.CreditMapper;
import com.sidework.payment.persistence.repository.CreditJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreditPersistenceAdapter implements CreditOutPort {
    private final CreditJpaRepository repo;

    @Override
    public Long findAmountByUser(Long userId) {
        return repo.findSumAmountByUserId(userId);
    }
}
