package com.sidework.project.application.port.in;

import com.sidework.project.application.dto.ProjectUserReviewCommand;

public interface ProjectUserReviewCommandUseCase {
	void create(Long reviewerUserId, Long projectId, ProjectUserReviewCommand command);
}
