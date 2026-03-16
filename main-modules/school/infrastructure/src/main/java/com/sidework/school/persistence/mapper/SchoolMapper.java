package com.sidework.school.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.school.domain.School;
import com.sidework.school.persistence.entity.SchoolEntity;

@Mapper(componentModel = "spring")
public interface SchoolMapper {

	School toDomain(SchoolEntity entity);

	SchoolEntity toEntity(School domain);
}

