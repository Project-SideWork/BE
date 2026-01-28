package com.sidework.skill.application.port.out;


import com.sidework.skill.domain.ProjectRequiredSkill;

import java.util.List;

public interface ProjectRequiredSkillOutPort {
    void saveAll(List<ProjectRequiredSkill> domains);
    boolean existsById(Long id);
    List<Long> findAllSkillIdsByProject(Long projectId);
    void deleteAll(List<ProjectRequiredSkill> domains);
}
