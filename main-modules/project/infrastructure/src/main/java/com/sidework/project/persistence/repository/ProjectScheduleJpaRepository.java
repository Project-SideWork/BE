package com.sidework.project.persistence.repository;

import com.sidework.project.persistence.entity.ProjectScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectScheduleJpaRepository extends JpaRepository<ProjectScheduleEntity, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            DELETE FROM ProjectScheduleEntity ps WHERE ps.projectId = :projectId
            """)
    void deleteAllByProjectId(@Param("projectId") Long projectId);
}
