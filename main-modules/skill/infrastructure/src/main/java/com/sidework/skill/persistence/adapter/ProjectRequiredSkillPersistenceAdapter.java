package com.sidework.skill.persistence.adapter;

import com.sidework.skill.application.port.out.ProjectRequiredSkillOutPort;
import com.sidework.skill.domain.ProjectRequiredSkill;
import com.sidework.skill.persistence.entity.ProjectRequiredSkillEntity;
import com.sidework.skill.persistence.mapper.ProjectRequiredSkillMapper;
import com.sidework.skill.persistence.repository.ProjectRequiredSkillJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectRequiredSkillPersistenceAdapter implements ProjectRequiredSkillOutPort {
    private final ProjectRequiredSkillJpaRepository repo;
    private final ProjectRequiredSkillMapper mapper;

    @Override
    public void saveAll(List<ProjectRequiredSkill> domains) {
        List<ProjectRequiredSkillEntity> entities = domains.stream()
                .map(mapper::toEntity)
                .toList();

        repo.saveAll(entities);
    }

    @Override
    public void deleteByProjectIdAndSkillIdIn(Long projectId, List<Long> deleted) {
        if (deleted == null || deleted.isEmpty()) {
            return;
        }
        repo.deleteByProjectIdAndSkillIdIn(projectId, deleted);
    }

    @Override
    public List<Long> findAllSkillIdsByProject(Long projectId) {
        return repo.findAllSkillByProjectId(projectId);
    }
}
