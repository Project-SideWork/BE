package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.ProjectPortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectPortfolioJpaRepository extends JpaRepository<ProjectPortfolioEntity, Long> {
	List<ProjectPortfolioEntity> findByProfileId(Long profileId);
	void deleteAllByProfileId(Long profileId);
	boolean existsByPortfolioIdAndProfileIdNot(Long portfolioId, Long profileId);
}

