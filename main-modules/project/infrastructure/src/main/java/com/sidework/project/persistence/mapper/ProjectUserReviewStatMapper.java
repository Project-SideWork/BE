package com.sidework.project.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sidework.project.domain.ProjectUserReviewStat;
import com.sidework.project.persistence.entity.ProjectUserReviewStatEntity;

@Mapper(componentModel = "spring")
public interface ProjectUserReviewStatMapper {

	ProjectUserReviewStat toDomain(ProjectUserReviewStatEntity entity);
	ProjectUserReviewStatEntity toEntity(ProjectUserReviewStat domain);
}

