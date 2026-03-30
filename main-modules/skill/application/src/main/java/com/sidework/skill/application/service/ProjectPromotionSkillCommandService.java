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
	public void create(Long userId, Long promotionId, Long projectId, List<Long> skillIds) {
		if (skillIds == null || skillIds.isEmpty()) {
			return;
		}
		List<ProjectPromotionSkill> domains = createPromotionSkills(userId, promotionId, projectId, skillIds);
		projectPromotionSkillRepository.saveAll(domains);
	}

	@Override
	public void update(Long userId, Long promotionId, Long projectId, List<Long> skillIds) {
		List<Long> requested = skillIds == null ? List.of() : skillIds;

		PromotionSkillChangeSet resolved = resolveSkillChanges(userId, promotionId, projectId, requested);

		if (!resolved.toAdd().isEmpty()) {
			projectPromotionSkillRepository.saveAll(resolved.toAdd());
		}

		if (!resolved.toRemoveIds().isEmpty()) {
			projectPromotionSkillRepository.deleteByPromotionIdAndSkillIdIn(promotionId, resolved.toRemoveIds());
		}

	}

	private List<ProjectPromotionSkill> createPromotionSkills(Long userId, Long promotionId, Long projectId, List<Long> skillIds) {
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
				.projectId(projectId)
				.promotionId(promotionId)
				.userId(userId)
				.skillId(id)
				.build())
			.toList();
	}

	private PromotionSkillChangeSet resolveSkillChanges(
		Long userId,
		Long promotionId,
		Long projectId,
		List<Long> skillIds
	) {
		Set<Long> originalIds = new HashSet<>(projectPromotionSkillRepository.findAllSkillIdsByPromotionId(promotionId));
		Set<Long> requestedIds = new HashSet<>(skillIds);

		Set<Long> activeSkillIds =
			new HashSet<>(skillRepo.findActiveSkillsByIdIn(skillIds));

		Set<Long> invalid = requestedIds.stream()
			.filter(id -> !activeSkillIds.contains(id))
			.collect(Collectors.toSet());

		if (!invalid.isEmpty()) {
			throw new InvalidCommandException(
				"존재하지 않거나 비활성화된 우대 기술 id: " + invalid
			);
		}

		List<ProjectPromotionSkill> toAdd = requestedIds.stream()
			.filter(id -> !originalIds.contains(id))
			.map(id -> ProjectPromotionSkill.builder()
				.projectId(projectId)
				.promotionId(promotionId)
				.userId(userId)
				.skillId(id)
				.build())
			.toList();

		List<Long> toRemoveSkillIds = originalIds.stream()
			.filter(id -> !requestedIds.contains(id))
			.toList();

		return new PromotionSkillChangeSet(toAdd, toRemoveSkillIds);
	}
}
