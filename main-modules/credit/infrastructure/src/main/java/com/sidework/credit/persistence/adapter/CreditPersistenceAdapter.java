package com.sidework.credit.persistence.adapter;

import com.sidework.credit.application.port.out.CreditOutPort;
import com.sidework.credit.domain.Credit;
import com.sidework.credit.persistence.entity.CreditEntity;
import com.sidework.credit.persistence.mapper.CreditMapper;
import com.sidework.credit.persistence.repository.CreditJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreditPersistenceAdapter implements CreditOutPort {
    private final CreditJpaRepository repo;
    private final CreditMapper mapper;

    @Override
    public void saveAll(List<Credit> domains) {
        List<CreditEntity> entities = domains.stream().map(mapper::toEntity).toList();
        repo.saveAll(entities);
    }

    @Override
    public Long findAmountByUser(Long userId) {
        return repo.findSumAmountByUserId(userId);
    }

    @Override
    public List<Credit> findAvailableCredits(Long userId) {
        List<CreditEntity> entities = repo.findAvailableCredits(userId);
        return entities.stream().map(mapper::toDomain).toList();
    }
}
