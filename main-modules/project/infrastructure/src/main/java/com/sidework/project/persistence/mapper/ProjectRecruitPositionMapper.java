package com.sidework.project.persistence.mapper;


import org.mapstruct.Mapper;

import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.domain.ProjectRecruitPosition;
import com.sidework.project.persistence.entity.ProjectRecruitPositionEntity;

@Mapper(componentModel = "spring")
public interface ProjectRecruitPositionMapper {
	ProjectRecruitPosition toDomain(ProjectRecruitPositionEntity entity);
	ProjectRecruitPositionEntity toEntity(ProjectRecruitPosition domain);
}
