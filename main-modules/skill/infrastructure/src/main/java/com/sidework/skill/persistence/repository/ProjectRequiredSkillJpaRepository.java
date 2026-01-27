package com.sidework.skill.persistence.repository;

import com.sidework.skill.persistence.entity.ProjectRequiredSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRequiredSkillJpaRepository extends JpaRepository<ProjectRequiredSkillEntity, Long> {
}
