package com.sidework.skill.application.port.in;

import java.util.List;
import java.util.Map;

import com.sidework.skill.domain.ProjectRequiredSkill;

public interface ProjectRequiredQueryUseCase {
	List<ProjectRequiredSkill> queryByProjectId(Long projectId);
	List<String> queryNamesByProjectId(Long projectId);
	Map<Long, List<String>> queryNamesByProjectIds(List<Long> projectIds);
}
