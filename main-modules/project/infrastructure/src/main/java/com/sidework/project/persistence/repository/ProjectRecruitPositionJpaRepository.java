package com.sidework.project.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.project.persistence.entity.ProjectRecruitPositionEntity;

@Repository
public interface ProjectRecruitPositionJpaRepository extends JpaRepository<ProjectRecruitPositionEntity, Long> {

	List<ProjectRecruitPositionEntity> findByProjectId(Long projectId);

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM ProjectRecruitPositionEntity WHERE projectId = :projectId")
	void deleteByProjectId(@Param("projectId") Long projectId);
}
