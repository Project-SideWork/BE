package com.sidework.project.persistence.repository;

import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.persistence.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, Long> {
    @Query(
            """
            SELECT new com.sidework.project.application.dto.ProjectTitleDto(p.id, p.title) FROM ProjectEntity p
            WHERE p.id IN (:ids)
            """
    )
    List<ProjectTitleDto> findProjectionsByIds(@Param("ids") List<Long> ids);
}
