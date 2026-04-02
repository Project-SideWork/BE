package com.sidework.project.persistence.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sidework.project.persistence.entity.ProjectPromotionEntity;
import com.sidework.project.persistence.repository.custom.CustomProjectPromotionJpaRepository;

public interface ProjectPromotionJpaRepository extends JpaRepository<ProjectPromotionEntity, Long>, CustomProjectPromotionJpaRepository {
	@Query("""
        SELECT COUNT(p) > 0
        FROM ProjectPromotionEntity p
        WHERE p.projectId = :projectId
        AND p.userId = :userId
        AND p.createdAt >= :from
    """)
	boolean existsRecentPromotion(@Param("projectId") Long projectId, @Param("userId") Long userId, @Param("from") Instant from);

	Optional<ProjectPromotionEntity> findByIdAndUserId(Long promotionId, Long userId);

	boolean existsByIdAndUserId(Long promotionId, Long userId);
}
