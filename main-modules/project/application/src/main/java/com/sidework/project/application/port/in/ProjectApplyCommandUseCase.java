package com.sidework.project.application.port.in;

public interface ProjectApplyCommandUseCase {
	void apply(Long userId,Long projectId,ProjectApplyCommand command);
}
