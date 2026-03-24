package com.sidework.project.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.project.persistence.entity.ProjectUserReviewStatEntity;

@Repository
public interface ProjectUserReviewStatJpaRepository extends JpaRepository<ProjectUserReviewStatEntity, Long> {
	@Modifying
	@Query("""
	UPDATE ProjectUserReviewStatEntity s
	SET s.ratingScore = s.ratingScore + :ratingScore,
		s.ratingCount = s.ratingCount + :ratingCount
	WHERE s.userId = :userId
""")
	int incrementStat(
		@Param("userId") Long userId,
		@Param("ratingScore") double ratingScore,
		@Param("ratingCount") long ratingCount
	);

}

