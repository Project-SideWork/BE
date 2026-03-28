package com.sidework.project.persistence.mapper;

import org.mapstruct.Mapper;

import com.sidework.project.domain.ProjectUserReview;
import com.sidework.project.persistence.entity.ProjectUserReviewEntity;

@Mapper(componentModel = "spring")
public interface ProjectUserReviewMapper {
	ProjectUserReview toDomain(ProjectUserReviewEntity entity);
	ProjectUserReviewEntity toEntity(ProjectUserReview domain);
}
