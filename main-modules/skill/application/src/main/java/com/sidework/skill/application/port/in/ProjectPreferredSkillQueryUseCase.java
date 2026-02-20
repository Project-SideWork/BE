package com.sidework.skill.application.port.in;

import java.util.List;

import com.sidework.skill.domain.ProjectPreferredSkill;

public interface ProjectPreferredSkillQueryUseCase {
	List<ProjectPreferredSkill> queryByProjectId(Long projectId);
	List<String> queryNamesByProjectId(Long projectId);
}
