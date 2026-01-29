package com.sidework.skill.application.port.in;


import java.util.List;

public interface ProjectPreferredSkillCommandUseCase {
    void create(Long projectId, List<Long> skillIds);
    void update(Long projectId, List<Long> skillIds);
}
