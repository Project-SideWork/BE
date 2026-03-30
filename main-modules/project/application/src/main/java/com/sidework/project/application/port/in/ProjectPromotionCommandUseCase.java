package com.sidework.project.application.port.in;

import com.sidework.project.application.dto.ProjectPromotionCommand;

public interface ProjectPromotionCommandUseCase {
	void create(Long userId, Long projectId, ProjectPromotionCommand command);
	void update(Long userId, Long promotionId, Long projectId, ProjectPromotionCommand command);
	void delete(Long userId, Long promotionId, Long projectId);
}
