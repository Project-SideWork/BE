package com.sidework.skill.persistence.repository;

import com.sidework.skill.persistence.entity.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillJpaRepository extends JpaRepository<SkillEntity, Long> {

    @Query("""
    SELECT s
    FROM SkillEntity s
    WHERE s.id IN :ids
      AND s.isActive = true and s.categoryId is not null
    """)
    List<SkillEntity> findByIdIn(@Param("ids") List<Long> ids);

    @Query("""
    SELECT s.id
    FROM SkillEntity s
    WHERE s.id IN :ids
      AND s.isActive = true and s.categoryId is not null
    """)
    List<Long> findActiveIdsByIdIn(@Param("ids") List<Long> ids);
}
