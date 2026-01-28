package com.sidework.skill.application.service;

import com.sidework.common.exception.InvalidCommandException;
import com.sidework.skill.application.port.in.ProjectRequiredCommandUseCase;
import com.sidework.skill.application.port.out.ProjectRequiredSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
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
public class ProjectRequiredSkillCommandService implements ProjectRequiredCommandUseCase {
    private final ProjectRequiredSkillOutPort repo;
    private final SkillOutPort skillRepo;


    @Override
    public void create(Long projectId, List<Long> skillIds) {
        List<ProjectRequiredSkill> domains = createRequiredSkills(projectId, skillIds);
        repo.saveAll(domains);
    }

    @Override
    public void update(Long projectId, List<Long> skillIds) {
        RequiredSkillChangeSet resolved = resolveSkillChanges(projectId, skillIds);

        if (!resolved.toAdd().isEmpty()) {
            repo.saveAll(resolved.toAdd());
        }

        if (!resolved.toRemoveIds().isEmpty()) {
            repo.deleteByProjectIdAndSkillIdIn(
                    projectId,
                    resolved.toRemoveIds()
            );
        }
    }

    private List<ProjectRequiredSkill> createRequiredSkills(Long projectId, List<Long> skillIds) {
        Set<Long> requested = new HashSet<>(skillIds);

        Set<Long> activeSkillIds =
                new HashSet<>(skillRepo.findActiveSkillsByIdIn(skillIds));

        Set<Long> invalid = requested.stream()
                .filter(id -> !activeSkillIds.contains(id))
                .collect(Collectors.toSet());

        if (!invalid.isEmpty()) {
            throw new InvalidCommandException(
                    "존재하지 않거나 비활성화된 필수 기술 id: " + invalid
            );
        }
        return requested.stream()
                .map(id -> ProjectRequiredSkill.builder()
                        .projectId(projectId)
                        .skillId(id)
                        .build())
                .toList();
    }

    private RequiredSkillChangeSet resolveSkillChanges(Long projectId, List<Long> skillIds) {

        Set<Long> originalIds =
                new HashSet<>(repo.findAllSkillIdsByProject(projectId));

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

        List<ProjectRequiredSkill> toAdd = requestedIds.stream()
                .filter(id -> !originalIds.contains(id))
                .map(id -> ProjectRequiredSkill.builder()
                        .projectId(projectId)
                        .skillId(id)
                        .build()
                )
                .toList();

        List<Long> toRemoveSkillIds = originalIds.stream()
                .filter(id -> !requestedIds.contains(id))
                .toList();

        return new RequiredSkillChangeSet(toAdd, toRemoveSkillIds);
    }
}
