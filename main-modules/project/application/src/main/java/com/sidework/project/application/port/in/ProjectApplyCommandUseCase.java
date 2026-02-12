package com.sidework.project.application.port.in;

public interface ProjectApplyCommandUseCase {
	void apply(Long userId, Long projectId, ProjectApplyCommand command);
	void approve(Long userId, Long projectId, ProjectApplyDecisionCommand command);
	void reject(Long userId, Long projectId, ProjectApplyDecisionCommand command);
}
