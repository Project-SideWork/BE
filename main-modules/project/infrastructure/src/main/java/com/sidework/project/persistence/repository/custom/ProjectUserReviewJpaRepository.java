package com.sidework.project.persistence.repository.custom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidework.project.persistence.entity.ProjectUserReviewEntity;

@Repository
public interface ProjectUserReviewJpaRepository extends JpaRepository<ProjectUserReviewEntity, Long> {
	boolean existsByProjectIdAndReviewerUserIdAndRevieweeUserId(Long projectId, Long reviewerId, Long revieweeUserId);
}
