package com.sidework.notification.persistence.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sidework.notification.persistence.entity.NotificationEntity;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
	List<NotificationEntity> findByUserId(Long userId);
	@Query("""
    select n from NotificationEntity n
    where n.userId = :userId
      and (
          :cursorCreatedAt is null
          or n.createdAt < :cursorCreatedAt
          or (n.createdAt = :cursorCreatedAt and n.id < :cursorId)
      )
    order by n.createdAt desc, n.id desc
    """)
	List<NotificationEntity> findByUserIdAndCursor(
		@Param("userId") Long userId,
		@Param("cursorCreatedAt") Instant cursorCreatedAt,
		@Param("cursorId") Long cursorId,
		Pageable pageable);
}
