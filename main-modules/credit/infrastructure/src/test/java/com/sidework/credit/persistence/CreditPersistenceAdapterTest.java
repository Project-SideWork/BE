package com.sidework.credit.persistence;

import com.sidework.credit.domain.Credit;
import com.sidework.credit.persistence.adapter.CreditPersistenceAdapter;
import com.sidework.credit.persistence.entity.CreditEntity;
import com.sidework.credit.persistence.mapper.CreditMapper;
import com.sidework.credit.persistence.repository.CreditJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditPersistenceAdapterTest {

    @Mock
    private CreditJpaRepository repo;

    @Mock
    private CreditMapper mapper;

    @InjectMocks
    private CreditPersistenceAdapter adapter;

    @Test
    void saveAll은_도메인_객체_리스트를_엔티티로_변환해_저장한다() {
        Credit domain1 = makeCredit(1L, 1000);
        Credit domain2 = makeCredit(2L, 2000);
        List<Credit> domains = List.of(domain1, domain2);

        CreditEntity entity1 = new CreditEntity();
        CreditEntity entity2 = new CreditEntity();

        when(mapper.toEntity(domain1)).thenReturn(entity1);
        when(mapper.toEntity(domain2)).thenReturn(entity2);

        adapter.saveAll(domains);

        verify(mapper).toEntity(domain1);
        verify(mapper).toEntity(domain2);
        verify(repo).saveAll(List.of(entity1, entity2));
    }

    @Test
    void findAmountByUser는_userId로_크레딧_합계를_반환한다() {
        Long userId = 1L;
        Integer expected = 3000;

        when(repo.findSumAmountByUserId(userId)).thenReturn(expected);

        Integer result = adapter.findAmountByUser(userId);

        assertEquals(expected, result);
        verify(repo).findSumAmountByUserId(userId);
    }

    @Test
    void findAmountByUser는_크레딧이_없으면_null을_반환한다() {
        Long userId = 1L;

        when(repo.findSumAmountByUserId(userId)).thenReturn(null);

        Integer result = adapter.findAmountByUser(userId);

        assertNull(result);
        verify(repo).findSumAmountByUserId(userId);
    }

    @Test
    void findAvailableCredits는_userId로_사용가능한_크레딧_목록을_반환한다() {
        Long userId = 1L;

        CreditEntity entity1 = new CreditEntity();
        CreditEntity entity2 = new CreditEntity();
        List<CreditEntity> entities = List.of(entity1, entity2);

        Credit domain1 = makeCredit(1L, 1000);
        Credit domain2 = makeCredit(2L, 2000);

        when(repo.findAvailableCredits(userId)).thenReturn(entities);
        when(mapper.toDomain(entity1)).thenReturn(domain1);
        when(mapper.toDomain(entity2)).thenReturn(domain2);

        List<Credit> result = adapter.findAvailableCredits(userId);

        assertEquals(2, result.size());
        assertEquals(domain1, result.get(0));
        assertEquals(domain2, result.get(1));

        verify(repo).findAvailableCredits(userId);
        verify(mapper).toDomain(entity1);
        verify(mapper).toDomain(entity2);
    }

    @Test
    void findAvailableCredits는_사용가능한_크레딧이_없으면_빈_리스트를_반환한다() {
        Long userId = 1L;

        when(repo.findAvailableCredits(userId)).thenReturn(List.of());

        List<Credit> result = adapter.findAvailableCredits(userId);

        assertTrue(result.isEmpty());
        verify(repo).findAvailableCredits(userId);
    }

    private Credit makeCredit(Long id, int amount) {
        return Credit.builder()
                .id(id)
                .amount(amount)
                .build();
    }
}
