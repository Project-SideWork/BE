package com.sidework.project.application.port.out;

import com.sidework.project.domain.Project;

public interface ProjectOutPort {
    void save(Project project);
}
