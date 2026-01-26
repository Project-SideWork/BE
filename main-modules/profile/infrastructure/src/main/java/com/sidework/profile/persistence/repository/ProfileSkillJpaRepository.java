package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.ProfileSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileSkillJpaRepository extends JpaRepository<ProfileSkillEntity, Long> {
	List<ProfileSkillEntity> findByProfileId(Long profileId);
}

