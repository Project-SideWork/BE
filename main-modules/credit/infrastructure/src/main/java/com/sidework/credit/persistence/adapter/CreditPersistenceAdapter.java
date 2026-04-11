package com.sidework.credit.persistence.adapter;

import com.sidework.credit.application.port.out.CreditOutPort;
import com.sidework.credit.persistence.repository.CreditJpaRepository;
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
