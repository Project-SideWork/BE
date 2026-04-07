package com.sidework.project.application.port.out;

import com.sidework.project.domain.ProjectRetrospective;

public interface ProjectRetrospectiveOutPort {
	Long save(ProjectRetrospective retrospective);

	boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}
