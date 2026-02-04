package com.sidework.notification.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.sidework.notification.domain.Notification;
import com.sidework.notification.persistence.entity.NotificationEntity;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
	Notification toDomain(NotificationEntity entity);
	NotificationEntity toEntity(Notification notification);
	void updateEntity(@MappingTarget NotificationEntity entity, Notification notification);
}
