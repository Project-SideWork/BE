package com.sidework.skill.application.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.skill.application.port.in.ProjectPromotionSkillQueryUseCase;
import com.sidework.skill.application.port.out.ProjectPromotionSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.Skill;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectPromotionSkillQueryService implements ProjectPromotionSkillQueryUseCase {
	private final ProjectPromotionSkillOutPort projectPromotionSkillOutPort;
	private final SkillOutPort skillOutPort;


	@Override
	public List<String> queryNamesByPromotionId(Long promotionId) {
		List<Long> skillIds = projectPromotionSkillOutPort.findAllSkillIdsByPromotionId(promotionId);

		List<Skill> skillList = skillOutPort.findByIdIn(skillIds);

		Map<Long, String> idToName = skillList.stream()
			.collect(Collectors.toMap(Skill::getId, Skill::getName));

		return skillIds.stream()
			.map(idToName::get)
			.filter(Objects::nonNull)
			.toList();
	}

}
