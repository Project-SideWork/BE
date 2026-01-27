package com.sidework.skill.application.port.out;

import com.sidework.skill.domain.ProjectPreferredSkill;

import java.util.List;

public interface ProjectPreferredSkillOutPort {
    void saveAll(List<ProjectPreferredSkill> domains);
    boolean existsById(Long id);
    List<Long> findAllSkillIdsByProject(Long projectId);
    void deleteAll(List<ProjectPreferredSkill> domains);
}
