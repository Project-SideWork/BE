package com.sidework.project.application.port.in;

public interface ProjectLikeCommandUseCase {
	void like(Long userId, Long projectId);
}
