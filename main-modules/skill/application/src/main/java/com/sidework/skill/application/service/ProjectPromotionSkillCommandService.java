package com.sidework.skill.application.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.common.exception.InvalidCommandException;
import com.sidework.skill.application.port.in.ProjectPromotionSkillCommandUseCase;
import com.sidework.skill.application.port.out.ProjectPromotionSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.ProjectPromotionSkill;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectPromotionSkillCommandService implements ProjectPromotionSkillCommandUseCase {

	private final SkillOutPort skillRepo;
	private final ProjectPromotionSkillOutPort projectPromotionSkillRepository;

	@Override
	public void create(Long userId, Long promotionId, List<Long> skillIds) {
		if (skillIds == null || skillIds.isEmpty()) {
			return;
		}
		List<ProjectPromotionSkill> domains = createPromotionSkills(userId, promotionId, skillIds);
		projectPromotionSkillRepository.saveAll(domains);
	}

	private List<ProjectPromotionSkill> createPromotionSkills(Long userId, Long promotionId, List<Long> skillIds) {
		Set<Long> requested = new HashSet<>(skillIds);

		Set<Long> activeSkillIds =
			new HashSet<>(skillRepo.findActiveSkillsByIdIn(skillIds));

		Set<Long> invalid = requested.stream()
			.filter(id -> !activeSkillIds.contains(id))
			.collect(Collectors.toSet());

		if (!invalid.isEmpty()) {
			throw new InvalidCommandException(
				"존재하지 않거나 비활성화된 우대 기술 id: " + invalid
			);
		}
		return requested.stream()
			.map(id -> ProjectPromotionSkill.builder()
				.projectId(promotionId)
				.userId(userId)
				.skillId(id)
				.build())
			.toList();
	}
}
