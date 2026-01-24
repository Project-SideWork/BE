package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.ProfileSchoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileSchoolJpaRepository extends JpaRepository<ProfileSchoolEntity, Long> {
	List<ProfileSchoolEntity> findByProfileId(Long profileId);
}
