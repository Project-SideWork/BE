package com.sidework.skill.application.port.out;


import com.sidework.skill.domain.ProjectRequiredSkill;

import java.util.List;

public interface ProjectRequiredSkillOutPort {
    void saveAll(List<ProjectRequiredSkill> domains);
    void deleteByProjectIdAndSkillIdIn(Long projectId, List<Long> deleted);
    List<Long> findAllSkillIdsByProject(Long projectId);
    List<ProjectRequiredSkill> getProjectRequiredSkills(Long projectId);
    List<ProjectRequiredSkill> getProjectRequiredSkillsByProjectIds(List<Long> projectIds);
}
