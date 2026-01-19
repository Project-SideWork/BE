package com.sidework.project.persistence.mapper;

import com.sidework.project.domain.ProjectUser;
import com.sidework.project.persistence.entity.ProjectUserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectUserMapper {
    ProjectUser toDomain(ProjectUserEntity entity);
    ProjectUserEntity toEntity(ProjectUser domain);
}
