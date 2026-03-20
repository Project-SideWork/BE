package com.sidework.profile.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.profile.persistence.entity.ProfileLikeEntity;

@Repository
public interface ProfileLikeRepository extends JpaRepository<ProfileLikeEntity, Long> {
	boolean existsByUserIdAndProfileId(Long userId, Long profileId);

	@Modifying
	@Query("""
    DELETE FROM ProfileLikeEntity pl
    WHERE pl.userId = :userId
      AND pl.profileId = :profileId
""")

	int deleteByUserIdAndProfileId(@Param("userId") Long userId, @Param("profileId") Long profileId);

	@Modifying
	@Query(
		value = """
			INSERT IGNORE INTO profile_likes (user_id, profile_id, created_at, updated_at)
			VALUES (:userId, :profileId, NOW(), NOW())
			""",
		nativeQuery = true
	)
	int insertIgnore(@Param("userId") Long userId, @Param("profileId") Long profileId);

	@Query("SELECT pl.profileId FROM ProfileLikeEntity pl WHERE pl.userId = :userId AND pl.profileId IN :profileIds")
	List<Long> findProfileIdsByUserIdAndProfileIdIn(
		@Param("userId") Long userId,
		@Param("profileIds") List<Long> profileIds
	);

	@Query("SELECT pl.profileId FROM ProfileLikeEntity pl WHERE pl.userId = :userId")
	List<Long> findProfileIdsByUserId(@Param("userId") Long userId);
}

