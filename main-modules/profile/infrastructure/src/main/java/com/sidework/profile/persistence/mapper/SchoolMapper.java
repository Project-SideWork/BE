package com.sidework.profile.persistence.mapper;

import com.sidework.profile.domain.School;
import com.sidework.profile.persistence.entity.SchoolEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
	School toDomain(SchoolEntity entity);
	SchoolEntity toEntity(School domain);
}

