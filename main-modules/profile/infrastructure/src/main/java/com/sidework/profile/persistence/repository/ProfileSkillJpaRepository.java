package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.ProfileSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileSkillJpaRepository extends JpaRepository<ProfileSkillEntity, Long> {
	List<ProfileSkillEntity> findByProfileId(Long profileId);
	List<ProfileSkillEntity> findByProfileIdIn(List<Long> profileIds);

	@Modifying
	@Query("DELETE FROM ProfileSkillEntity e WHERE e.profileId = :profileId")
	void deleteAllByProfileId(@Param("profileId") Long profileId);
}

