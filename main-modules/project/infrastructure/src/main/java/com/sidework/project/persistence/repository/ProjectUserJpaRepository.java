package com.sidework.project.persistence.repository;

import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.persistence.entity.ProjectUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    Optional<ProjectUserEntity> findFirstByProjectIdAndUserId(Long projectId, Long userId);

    Optional<ProjectUserEntity> findFirstByProjectIdAndUserIdAndRole(Long projectId, Long userId, ProjectRole role);

    List<ProjectUserEntity> findAllByProjectId(Long projectId);

    List<ProjectUserEntity> findByUserIdAndProjectIdIn(Long userId, List<Long> projectIds);

    List<ProjectUserEntity> findByProjectIdInAndRole(List<Long> projectIds, ProjectRole role);

    @Query("""
        SELECT pu.projectId, pu.userId FROM ProjectUserEntity pu
        WHERE pu.projectId IN :projectIds AND pu.role = :role
        """)
    List<Object[]> findOwnerProjectIdAndUserIdByProjectIdIn(@Param("projectIds") List<Long> projectIds, @Param("role") ProjectRole role);

    Optional<ProjectUserEntity> findFirstByProjectIdAndUserIdAndStatus(Long projectId, Long userId, ApplyStatus status);


    @Query("""
		SELECT NEW com.sidework.project.application.dto.ProjectTitleDto(p.id, p.title)
		FROM ProjectUserEntity pu
		JOIN ProjectEntity p ON p.id = pu.projectId
		WHERE pu.userId = :userId
		  AND p.status = com.sidework.project.domain.ProjectStatus.FINISHED
		"""
    )
    List<ProjectTitleDto> findFinishedProjectTitlesByUserId(@Param("userId") Long userId);
}
