package com.sidework.skill.application.port.out;

import com.sidework.skill.domain.Skill;

import java.util.List;

public interface SkillOutPort {
    Skill findById(Long id);
    List<Skill> findByIdIn(List<Long> ids);
    boolean existsById(Long id);
    List<Long> findIdsByIdIn(List<Long> ids);
}

