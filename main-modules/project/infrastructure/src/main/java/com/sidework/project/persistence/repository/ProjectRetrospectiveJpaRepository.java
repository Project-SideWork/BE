package com.sidework.project.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidework.project.domain.ProjectRetrospective;
import com.sidework.project.persistence.entity.ProjectRetrospectiveEntity;

@Repository
public interface ProjectRetrospectiveJpaRepository extends JpaRepository<ProjectRetrospectiveEntity, Long> {
	boolean existsByProjectIdAndUserId(Long projectId, Long userId);
	Optional<ProjectRetrospectiveEntity> findByProjectIdAndUserId(Long projectId, Long userId);
}
