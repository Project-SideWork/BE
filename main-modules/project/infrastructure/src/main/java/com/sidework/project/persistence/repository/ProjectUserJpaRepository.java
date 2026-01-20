package com.sidework.project.persistence.repository;

import com.sidework.project.domain.ProjectRole;
import com.sidework.project.persistence.entity.ProjectUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectUserJpaRepository extends JpaRepository<ProjectUserEntity, Long> {
    @Query(
            """
            SELECT pu.projectId FROM ProjectUserEntity pu
            WHERE pu.userId = :userId
            """
    )
    List<Long> findAllIdsByUserId(@Param("userId") Long userId);

    @Query(
            """
            SELECT pu.role FROM ProjectUserEntity pu
            WHERE pu.userId = :userId AND pu.projectId = :projectId
            """
    )
    List<ProjectRole> findAllRolesByUserAndProject(@Param("userId") Long userId, @Param("projectId") Long projectId);
}
