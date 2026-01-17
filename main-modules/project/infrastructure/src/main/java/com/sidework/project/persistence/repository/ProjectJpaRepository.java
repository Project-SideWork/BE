package com.sidework.project.persistence.repository;

import com.sidework.project.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
}
