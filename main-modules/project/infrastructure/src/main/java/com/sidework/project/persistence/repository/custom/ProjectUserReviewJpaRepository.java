package com.sidework.project.persistence.repository.custom;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.project.persistence.entity.ProjectUserReviewEntity;

@Repository
public interface ProjectUserReviewJpaRepository extends JpaRepository<ProjectUserReviewEntity, Long> {
	boolean existsByProjectIdAndReviewerUserIdAndRevieweeUserId(Long projectId, Long reviewerId, Long revieweeUserId);
	@Query("""
    SELECT r
    FROM ProjectUserReviewEntity r
    WHERE r.projectId IN :projectIds
      AND r.revieweeUserId = :userId
""")
	List<ProjectUserReviewEntity> findAllByProjectIdsAndRevieweeUserId(
		@Param("projectIds") List<Long> projectIds,
		@Param("userId") Long userId
	);
}
