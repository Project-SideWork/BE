package com.sidework.project.persistence.repository;

import com.sidework.project.persistence.entity.ProjectScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectScheduleJpaRepository extends JpaRepository<ProjectScheduleEntity, Long> {
}
