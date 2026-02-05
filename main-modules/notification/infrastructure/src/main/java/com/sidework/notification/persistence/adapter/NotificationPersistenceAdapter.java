package com.sidework.notification.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sidework.notification.application.exception.NotificationNotFoundException;
import com.sidework.notification.application.port.out.NotificationOutPort;
import com.sidework.notification.domain.Notification;
import com.sidework.notification.persistence.entity.NotificationEntity;
import com.sidework.notification.persistence.mapper.NotificationMapper;
import com.sidework.notification.persistence.repository.NotificationJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationPersistenceAdapter implements NotificationOutPort {

	private final NotificationMapper mapper;
	private final NotificationJpaRepository repository;

	@Override
	public void save(Notification notification) {
		if(notification == null) {
			return;
		}
		repository.save(mapper.toEntity(notification));
	}

	@Override
	public void update(Notification notification) {
		if(notification == null) {
			return;
		}
		NotificationEntity entity =  repository.findById(notification.getId())
			.orElseThrow(() -> new NotificationNotFoundException(notification.getId()));
		if (notification.isRead()) {
			entity.markAsRead();
		}
		repository.save(entity);
	}

	@Override
	public List<Notification> findByUserId(Long userId) {
		return repository.findByUserId(userId).stream()
			.map(mapper::toDomain)
			.toList();
	}

	@Override
	public Notification findById(Long id) {
		return repository.findById(id)
			.map(mapper::toDomain)
			.orElse(null);
	}
}
