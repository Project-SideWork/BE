package com.sidework.skill.application.port.in;


public interface ProjectPreferredSkillCommandUseCase {
    void create(Long projectId, ProjectPreferredSkillCommand command);
    void update(Long projectId, ProjectPreferredSkillCommand command);
}
