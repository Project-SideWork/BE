package com.sidework.credit.persistence.repository;

import com.sidework.credit.persistence.entity.CreditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditJpaRepository extends JpaRepository<CreditEntity, Long> {

    @Query("""
            SELECT COALESCE(SUM(c.remainingAmount), 0) FROM CreditEntity c
            WHERE c.userId = :userId
            """)
    Long findSumAmountByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT c FROM CreditEntity c
        WHERE c.userId = :userId
        AND c.remainingAmount > 0
        AND (c.expiresAt IS NULL OR c.expiresAt > CURRENT_TIMESTAMP)
        ORDER BY c.expiresAt ASC
        """)
    List<CreditEntity> findAvailableCredits(@Param("userId") Long userId);
}
