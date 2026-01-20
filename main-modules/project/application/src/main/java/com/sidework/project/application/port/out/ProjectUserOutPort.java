package com.sidework.project.application.port.out;


import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectUser;

import java.util.List;

public interface ProjectUserOutPort {
    void save(ProjectUser projectUser);
    List<ProjectRole> queryUserRolesByProject(Long userId, Long projectId);
    List<Long> queryAllProjectIds(Long userId);
}
