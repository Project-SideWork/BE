package com.sidework.project.persistence.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sidework.project.application.dto.ProjectPromotionListRow;
import com.sidework.project.persistence.repository.condition.ProjectPromotionSearchCondition;

public interface CustomProjectPromotionJpaRepository {
	Page<ProjectPromotionListRow> searchPromotions(ProjectPromotionSearchCondition condition, Pageable pageable);
}
