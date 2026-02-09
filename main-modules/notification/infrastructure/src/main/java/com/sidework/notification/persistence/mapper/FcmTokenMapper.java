package com.sidework.notification.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sidework.notification.domain.FcmToken;
import com.sidework.notification.persistence.entity.FcmTokenEntity;

@Mapper(componentModel = "spring")
public interface FcmTokenMapper {

	@Mapping(target = "id", ignore = true)
	FcmTokenEntity toEntity(FcmToken domain);

	FcmToken toDomain(FcmTokenEntity entity);
}
