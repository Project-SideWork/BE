package com.sidework.notification.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sidework.notification.domain.FcmUserToken;
import com.sidework.notification.persistence.entity.FcmTokenEntity;

@Mapper(componentModel = "spring")
public interface FcmTokenMapper {

	@Mapping(target = "id", ignore = true)
	FcmTokenEntity toEntity(FcmUserToken domain);

	FcmUserToken toDomain(FcmTokenEntity entity);
}
