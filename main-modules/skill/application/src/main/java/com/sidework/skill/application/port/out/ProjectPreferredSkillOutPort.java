package com.sidework.skill.application.port.out;

import com.sidework.skill.domain.ProjectPreferredSkill;

import java.util.List;

public interface ProjectPreferredSkillOutPort {
    void saveAll(List<ProjectPreferredSkill> domains);
    void deleteByProjectIdAndSkillIdIn(Long projectId, List<Long> deleted);
    List<Long> findAllSkillIdsByProject(Long projectId);
}
