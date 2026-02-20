package com.sidework.skill.persistence.repository;

import com.sidework.skill.persistence.entity.ProjectPreferredSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectPreferredSkillJpaRepository extends JpaRepository<ProjectPreferredSkillEntity, Long> {
    @Query("""
            SELECT p.skillId FROM ProjectPreferredSkillEntity p
            WHERE p.projectId = :projectId
            """)
    List<Long> findAllSkillByProjectId(@Param("projectId") Long projectId);

    @Modifying
    @Query("""
            DELETE FROM ProjectPreferredSkillEntity p
            WHERE p.projectId = :projectId and p.skillId in :ids
            """)
    void deleteByProjectIdAndSkillIdIn(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);

    List<ProjectPreferredSkillEntity> findAllByProjectId(Long projectId);
}
