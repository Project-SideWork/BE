package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.ProfileRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRoleJpaRepository extends JpaRepository<ProfileRoleEntity, Long> {
	List<ProfileRoleEntity> findByProfileId(Long profileId);
}
