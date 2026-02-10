package com.sidework.notification.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sidework.notification.persistence.entity.FcmTokenEntity;

import jakarta.persistence.LockModeType;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, Long> {

	Optional<FcmTokenEntity> findByUserIdAndToken(Long userId, String token);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select count(t) from FcmTokenEntity t where t.userId = :userId")
	long countByUserIdForUpdate(@Param("userId") Long userId);

	@Query("""
		select t from FcmTokenEntity t
		where t.userId = :userId
		order by t.updatedAt asc
		""")
	List<FcmTokenEntity> findByUserIdOrderByUpdatedAtAsc(@Param("userId") Long userId);

	List<FcmTokenEntity> findByUserIdAndPushAgreedTrue(Long userId);
}
