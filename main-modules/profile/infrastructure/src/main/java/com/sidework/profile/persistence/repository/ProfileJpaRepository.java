package com.sidework.profile.persistence.repository;

import com.sidework.profile.persistence.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, Long> {
	Optional<ProfileEntity> findByUserId(Long userId);
	boolean existsByUserId(Long userId);
	boolean existsByIdAndUserId(Long profileId, Long userId);
}
