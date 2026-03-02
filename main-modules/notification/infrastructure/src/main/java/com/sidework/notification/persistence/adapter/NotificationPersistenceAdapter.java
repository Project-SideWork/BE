package com.sidework.notification.persistence.adapter;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.sidework.notification.application.port.out.NotificationOutPort;
import com.sidework.notification.application.port.out.NotificationPage;
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
		NotificationEntity entity =  repository.findById(notification.getId()).orElse(null);
		if(entity == null) {
			return;
		}
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

	@Override
	public NotificationPage findByUserIdAndCursor(Long userId, Instant cursorCreatedAt, Long cursorId, int size) {
		List<NotificationEntity> entities = repository.findByUserIdAndCursor(
			userId, cursorCreatedAt, cursorId, PageRequest.of(0, size + 1));

		boolean hasNext = entities.size() > size;
		List<NotificationEntity> items = entities.stream().limit(size).toList();

		NotificationEntity last = hasNext && !items.isEmpty() ? items.get(items.size() - 1) : null;
		return new NotificationPage(
			items.stream().map(mapper::toDomain).toList(),
			hasNext,
			last != null ? last.getCreatedAt() : null,
			last != null ? last.getId() : null
		);
	}
}
