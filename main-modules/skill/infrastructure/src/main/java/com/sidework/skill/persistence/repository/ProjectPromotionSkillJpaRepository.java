package com.sidework.skill.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidework.skill.persistence.entity.ProjectPromotionSkillEntity;

@Repository
public interface ProjectPromotionSkillJpaRepository extends JpaRepository<ProjectPromotionSkillEntity, Long> {
}
