package com.sidework.skill.persistence.adapter;

import com.sidework.skill.persistence.mapper.ProjectRequiredSkillMapper;
import com.sidework.skill.persistence.repository.ProjectRequiredSkillJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectRequiredSkillPersistenceAdapter {
    private final ProjectRequiredSkillJpaRepository repo;
    private final ProjectRequiredSkillMapper mapper;
}
