package com.sidework.project.application.port.in;

public interface ProjectLikeCommandUseCase {
	void like(Long userId, Long projectId);
    void delete(Long userId, Long projectId);
}
