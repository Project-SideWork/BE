package com.sidework.project.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.project.domain.ProjectRetrospective;
import com.sidework.project.persistence.entity.ProjectRetrospectiveEntity;

@Mapper(componentModel = "spring")
public interface ProjectRetrospectiveMapper {
	ProjectRetrospective toDomain(ProjectRetrospectiveEntity entity);
	ProjectRetrospectiveEntity toEntity(ProjectRetrospective entity);
}
