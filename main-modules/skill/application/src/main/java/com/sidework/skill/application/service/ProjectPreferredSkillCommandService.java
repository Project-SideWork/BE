package com.sidework.skill.application.service;


import com.sidework.common.exception.InvalidCommandException;
import com.sidework.skill.application.port.in.ProjectPreferredSkillCommandUseCase;
import com.sidework.skill.application.port.out.ProjectPreferredSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.ProjectPreferredSkill;
import com.sidework.skill.domain.ProjectRequiredSkill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectPreferredSkillCommandService implements ProjectPreferredSkillCommandUseCase {
    private final ProjectPreferredSkillOutPort repo;
    private final SkillOutPort skillRepo;

    @Override
    public void create(Long projectId, List<Long> skillIds) {
        List<ProjectPreferredSkill> domains = createPreferredSkills(projectId, skillIds);
        repo.saveAll(domains);
    }

    @Override
    public void update(Long projectId, List<Long> skillIds) {
        PreferredSkillChangeSet categorized = resolveSkillChanges(projectId, skillIds);
        repo.saveAll(categorized.toAdd());
        repo.deleteAll(categorized.toRemove());
    }

    private List<ProjectPreferredSkill> createPreferredSkills(Long projectId, List<Long> skillIds) {
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
                .map(id -> ProjectPreferredSkill.builder()
                        .projectId(projectId)
                        .skillId(id)
                        .build())
                .toList();
    }

    private PreferredSkillChangeSet resolveSkillChanges(Long projectId, List<Long> skillIds) {
        // 원래 저장되어 있었던 ProjectPreferredSkill의 Skill ID 목록.
        Set<Long> originalIds = new HashSet<>(
                repo.findAllSkillIdsByProject(projectId)
        );

        Set<Long> requestedIds = new HashSet<>(skillIds);

        Set<Long> activeSkillIds =
                new HashSet<>(skillRepo.findActiveSkillsByIdIn(skillIds));

        Set<Long> invalid = requestedIds.stream()
                .filter(id -> !activeSkillIds.contains(id))
                .collect(Collectors.toSet());

        if (!invalid.isEmpty()) {
            throw new InvalidCommandException(
                    "존재하지 않거나 비활성화된 기술 id: " + invalid
            );
        }

        List<ProjectPreferredSkill> toAdd = requestedIds.stream()
                .filter(id -> !originalIds.contains(id))
                .map(id -> ProjectPreferredSkill.builder()
                        .projectId(projectId)
                        .skillId(id)
                        .build()
                )
                .toList();

        List<ProjectPreferredSkill> toRemove = originalIds.stream()
                .filter(id -> !requestedIds.contains(id))
                .map(id -> ProjectPreferredSkill.builder()
                        .projectId(projectId)
                        .skillId(id)
                        .build()
                )
                .toList();
        return new PreferredSkillChangeSet(toAdd, toRemove);
    }
}
