package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillJpaRepository extends JpaRepository<SkillEntity, Long> {
}

