package com.sidework.project.application.port.out;

import java.util.List;
import java.util.Map;

import com.sidework.project.domain.ProjectLike;

public interface ProjectLikeOutPort {
	void like(ProjectLike like);
	void unlike(Long userId, Long projectId);
	boolean isLiked(Long userId, Long projectId);
	Map<Long, Boolean> getLikes(Long userId, List<Long> projectIds);
	List<Long> findLikedProjectIds(Long userId);
}
