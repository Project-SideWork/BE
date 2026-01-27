package com.sidework.skill.persistence.adapter;

import com.sidework.skill.application.port.out.ProjectPreferredSkillOutPort;
import com.sidework.skill.domain.ProjectPreferredSkill;
import com.sidework.skill.persistence.entity.ProjectPreferredSkillEntity;
import com.sidework.skill.persistence.mapper.ProjectPreferredSkillMapper;
import com.sidework.skill.persistence.repository.ProjectPreferredSkillJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectPreferredSkillPersistenceAdapter implements ProjectPreferredSkillOutPort {
    private final ProjectPreferredSkillJpaRepository repo;
    private final ProjectPreferredSkillMapper mapper;

    @Override
    public void saveAll(List<ProjectPreferredSkill> domains) {
        List<ProjectPreferredSkillEntity> entities = domains.stream()
                .map(mapper::toEntity)
                .toList();

        repo.saveAll(entities);
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    @Override
    public List<Long> findAllSkillIdsByProject(Long projectId) {
       return repo.findAllSkillByProjectId(projectId);
    }

    @Override
    public void deleteAll(List<ProjectPreferredSkill> domains) {
        List<ProjectPreferredSkillEntity> entities = domains.stream().map(
                mapper::toEntity
        ).toList();
        repo.deleteAll(entities);
    }
}
