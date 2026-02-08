package com.sidework.notification.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sidework.notification.persistence.entity.FcmTokenEntity;

import jakarta.persistence.LockModeType;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, Long> {

	Optional<FcmTokenEntity> findByUserIdAndToken(Long userId, String token);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select count(t) from FcmTokenEntity t where t.userId = :userId")
	long countByUserIdForUpdate(@Param("userId") Long userId);

	@Modifying
	@Query("""
    delete from FcmTokenEntity t
    where t.id = (
        select t2.id from FcmTokenEntity t2
        where t2.userId = :userId
        order by t2.updatedAt asc
        limit 1
    )
""")
	void deleteOldestTokenByUserId(@Param("userId") Long userId);
}
