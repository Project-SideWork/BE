package com.sidework.skill.application.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.skill.application.port.in.ProjectRequiredQueryUseCase;
import com.sidework.skill.application.port.out.ProjectRequiredSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.ProjectRequiredSkill;
import com.sidework.skill.domain.Skill;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectRequiredSkillQueryService implements ProjectRequiredQueryUseCase {
	private final ProjectRequiredSkillOutPort projectRequiredSkillRepository;
	private final SkillOutPort skillOutPort;

	@Override
	public List<ProjectRequiredSkill> queryByProjectId(Long projectId) {
		List<ProjectRequiredSkill> skills = projectRequiredSkillRepository.getProjectRequiredSkills(projectId);
		if (skills == null){
			return List.of();
		}
		return skills;
	}

	@Override
	public List<String> queryNamesByProjectId(Long projectId) {
		List<ProjectRequiredSkill> skills = projectRequiredSkillRepository.getProjectRequiredSkills(projectId);
		if (skills == null || skills.isEmpty()) {
			return List.of();
		}
		List<Long> skillIds = skills.stream()
			.map(ProjectRequiredSkill::getSkillId)
			.toList();

		List<Skill> skillList = skillOutPort.findByIdIn(skillIds);

		Map<Long, String> idToName = skillList.stream()
			.collect(Collectors.toMap(Skill::getId, Skill::getName));

		return skills.stream()
			.map(skill -> idToName.get(skill.getSkillId()))
			.filter(Objects::nonNull)
			.toList();
	}
}
