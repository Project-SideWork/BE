package com.sidework.skill.persistence.adapter;

import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.Skill;
import com.sidework.skill.persistence.entity.SkillEntity;
import com.sidework.skill.persistence.exception.SkillNotFoundException;
import com.sidework.skill.persistence.mapper.SkillMapper;
import com.sidework.skill.persistence.repository.SkillJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillPersistenceAdapter implements SkillOutPort {

    private final SkillJpaRepository repo;
    private final SkillMapper skillMapper;

    @Override
    public List<Long> findActiveSkillsByIdIn(List<Long> ids) {
        return repo.findActiveIdsByIdIn(ids);
    }

    @Override
    public Skill findById(Long id) {
        SkillEntity entity = repo.findById(id)
                .orElseThrow(() -> new SkillNotFoundException(id));
        return skillMapper.toDomain(entity);
    }

    @Override
    public List<Skill> findByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<SkillEntity> entities = repo.findByIdIn(ids);
        return entities.stream()
                .map(skillMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }
}
