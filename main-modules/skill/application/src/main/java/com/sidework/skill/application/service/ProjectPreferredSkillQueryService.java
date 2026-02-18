package com.sidework.skill.application.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.skill.application.port.in.ProjectPreferredSkillQueryUseCase;
import com.sidework.skill.application.port.out.ProjectPreferredSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.ProjectPreferredSkill;
import com.sidework.skill.domain.Skill;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectPreferredSkillQueryService implements ProjectPreferredSkillQueryUseCase {

	private final ProjectPreferredSkillOutPort projectPreferredRepository;
	private final SkillOutPort skillOutPort;

	@Override
	public List<ProjectPreferredSkill> queryByProjectId(Long projectId) {
		List<ProjectPreferredSkill> skills = projectPreferredRepository.getProjectPreferredSkills(projectId);
		if(skills == null){
			return List.of();
		}
		return skills;
	}

	@Override
	public List<String> queryNamesByProjectId(Long projectId) {
		List<ProjectPreferredSkill> skills = projectPreferredRepository.getProjectPreferredSkills(projectId);
		if (skills == null || skills.isEmpty()) {
			return List.of();
		}
		List<Long> skillIds = skills.stream()
			.map(ProjectPreferredSkill::getSkillId)
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
