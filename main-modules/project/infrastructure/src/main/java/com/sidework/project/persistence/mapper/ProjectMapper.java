package com.sidework.project.persistence.mapper;

import com.sidework.project.domain.Project;
import com.sidework.project.persistence.entity.ProjectEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toDomain(ProjectEntity entity);
    ProjectEntity toEntity(Project domain);
}
