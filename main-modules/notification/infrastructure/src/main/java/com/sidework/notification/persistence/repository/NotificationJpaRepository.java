package com.sidework.notification.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sidework.notification.persistence.entity.NotificationEntity;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
	List<NotificationEntity> findByUserId(Long userId);
}
