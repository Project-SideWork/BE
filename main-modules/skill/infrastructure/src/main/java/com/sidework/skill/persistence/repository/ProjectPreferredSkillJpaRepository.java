package com.sidework.skill.persistence.repository;

import com.sidework.skill.persistence.entity.ProjectPreferredSkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
