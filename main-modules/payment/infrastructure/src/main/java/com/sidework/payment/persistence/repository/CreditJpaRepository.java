package com.sidework.payment.persistence.repository;

import com.sidework.payment.persistence.entity.CreditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditJpaRepository extends JpaRepository<CreditEntity, Long> {
    @Query("""
            SELECT COALESCE(SUM(c.amount), 0) FROM CreditEntity c
            WHERE c.userId = :userId
            """)
    Long findSumAmountByUserId(@Param("userId") Long userId);

}
