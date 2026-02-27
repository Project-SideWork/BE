package com.sidework.project.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidework.project.persistence.entity.ProjectLikeEntity;

@Repository
public interface ProjectLikeRepository extends JpaRepository<ProjectLikeEntity, Long> {
	boolean existsByUserIdAndProjectId(Long userId, Long projectId);
	void deleteByUserIdAndProjectId(Long userId, Long projectId);
}
