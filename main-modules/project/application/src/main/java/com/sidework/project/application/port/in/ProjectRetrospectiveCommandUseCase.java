package com.sidework.project.application.port.in;

import jakarta.validation.Valid;

public interface ProjectRetrospectiveCommandUseCase {
	void create(Long userId, Long projectId, ProjectRetrospectiveCommand command);
}
