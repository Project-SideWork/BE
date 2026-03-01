package com.sidework.project.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.project.persistence.entity.ProjectLikeEntity;

@Repository
public interface ProjectLikeRepository extends JpaRepository<ProjectLikeEntity, Long> {
	boolean existsByUserIdAndProjectId(Long userId, Long projectId);
	void deleteByUserIdAndProjectId(Long userId, Long projectId);

	@Query("SELECT pl.projectId FROM ProjectLikeEntity pl WHERE pl.userId = :userId AND pl.projectId IN :projectIds")
	List<Long> findProjectIdsByUserIdAndProjectIdIn(@Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);

}
