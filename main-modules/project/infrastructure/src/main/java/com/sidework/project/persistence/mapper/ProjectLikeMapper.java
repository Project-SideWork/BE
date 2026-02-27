package com.sidework.project.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.project.domain.ProjectLike;
import com.sidework.project.persistence.entity.ProjectLikeEntity;

@Mapper(componentModel = "spring")
public interface ProjectLikeMapper {
	ProjectLike toDomain(ProjectLikeEntity entity);
	ProjectLikeEntity toEntity(ProjectLike entity);
}
