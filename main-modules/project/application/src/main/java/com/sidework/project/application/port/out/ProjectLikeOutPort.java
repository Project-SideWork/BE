package com.sidework.project.application.port.out;

import java.util.List;
import java.util.Map;

import com.sidework.project.domain.ProjectLike;

public interface ProjectLikeOutPort {
	void like(ProjectLike like);
	void unlike(ProjectLike like);
	boolean isLiked(Long userId, Long projectId);
	Map<Long, Boolean> getLikes(Long userId, List<Long> projectIds);
}
