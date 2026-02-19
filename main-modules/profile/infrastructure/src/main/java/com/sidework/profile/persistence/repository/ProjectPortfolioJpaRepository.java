package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.ProjectPortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectPortfolioJpaRepository extends JpaRepository<ProjectPortfolioEntity, Long> {
	List<ProjectPortfolioEntity> findByProfileId(Long profileId);
	void deleteAllByProfileId(Long profileId);
	boolean existsByPortfolioIdAndProfileIdNot(Long portfolioId, Long profileId);

	@Query("SELECT DISTINCT pp.portfolioId FROM ProjectPortfolioEntity pp WHERE pp.portfolioId IN :portfolioIds AND pp.profileId <> :excludeProfileId")
	List<Long> findPortfolioIdsReferencedByOtherProfiles(@Param("excludeProfileId") Long excludeProfileId, @Param("portfolioIds") List<Long> portfolioIds);
}

