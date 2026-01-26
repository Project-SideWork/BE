package com.sidework.profile.persistence.repository;

import java.util.List;

import com.sidework.profile.persistence.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillJpaRepository extends JpaRepository<SkillEntity, Long> {
	List<SkillEntity> findByIdIn(List<Long> ids);
}

