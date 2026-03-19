package com.sidework.project.persistence.repository.custom;

import com.sidework.project.persistence.entity.ProjectEntity;
import com.sidework.project.persistence.repository.condition.ProjectSearchCondition;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CustomProjectJpaRepository {

    Page<ProjectEntity> searchByKeywordAndSkillIdsQuerydsl(
        ProjectSearchCondition condition,
        Pageable pageable);

	Page<ProjectEntity> searchByKeywordAndSkillIdsInProjectIdsQuerydsl(
		ProjectSearchCondition condition,
		Pageable pageable);

	Page<ProjectEntity> searchLikedByKeywordAndSkillIdsQuerydsl(
		Long userId,
		ProjectSearchCondition condition,
		Pageable pageable);
}
