package com.sidework.project.application.port.out;

import com.sidework.project.domain.ProjectLike;

public interface ProjectLikeOutPort {
	void like(ProjectLike like);
	void unlike(ProjectLike like);
	boolean isLiked(Long userId, Long projectId);
}
