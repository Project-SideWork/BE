package com.sidework.skill.application.service;


import com.sidework.common.exception.InvalidCommandException;
import com.sidework.skill.application.port.in.ProjectPreferredSkillCommandUseCase;
import com.sidework.skill.application.port.out.ProjectPreferredSkillOutPort;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.ProjectPreferredSkill;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
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
        List<ProjectPreferredSkill> domains = convert(projectId, skillIds);
        repo.saveAll(domains);
    }

    @Override
    public void update(Long projectId, List<Long> skillIds) {
        Pair<List<ProjectPreferredSkill>, List<ProjectPreferredSkill>> categorized = categorize(projectId, skillIds);
        repo.saveAll(categorized.getLeft());
        repo.deleteAll(categorized.getRight());
    }

    private List<ProjectPreferredSkill> convert(Long projectId, List<Long> skillIds) {
        return skillIds.stream()
                .map(id -> {
                        if(!skillRepo.existsById(id)) {
                            throw new InvalidCommandException(String.format("추가하고자 하는 기술(id=%d)을 찾을 수 없습니다.", id));
                        }
                        return ProjectPreferredSkill.builder()
                                    .projectId(projectId)
                                    .skillId(id)
                                    .build();
                }
                ).toList();
    }

    private Pair<List<ProjectPreferredSkill>, List<ProjectPreferredSkill>> categorize(Long projectId, List<Long> skillIds) {
        // 원래 저장되어 있었던 ProjectPreferredSkill의 Skill ID 목록.
        Set<Long> originalIds = new HashSet<>(
                repo.findAllSkillIdsByProject(projectId)
        );

        Set<Long> requestedIds = new HashSet<>(skillIds);

        Set<Long> existingSkillIds = new HashSet<>(skillRepo.findIdsByIdIn(requestedIds.stream().toList()));

        Set<Long> missing = requestedIds.stream()
                .filter(id -> !existingSkillIds.contains(id))
                .collect(Collectors.toSet());

        if (!missing.isEmpty()) {
            throw new InvalidCommandException("존재하지 않는 기술 id: " + missing);
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
        return Pair.of(toAdd, toRemove);
    }
}
