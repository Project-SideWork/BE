package com.sidework.project.application.port.in;

import java.util.List;
import java.util.Map;

public interface ProjectLikeQueryUseCase {
	Map<Long, Boolean> isLikedByProjectIds(Long userId, List<Long> projectIds);

}
