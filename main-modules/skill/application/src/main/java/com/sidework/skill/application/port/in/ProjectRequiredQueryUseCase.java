package com.sidework.skill.application.port.in;

import java.util.List;

import com.sidework.skill.domain.ProjectPreferredSkill;
import com.sidework.skill.domain.ProjectRequiredSkill;

public interface ProjectRequiredQueryUseCase {
	List<ProjectRequiredSkill> queryByProjectId(Long projectId);
	List<String> queryNamesByProjectId(Long projectId);
}
