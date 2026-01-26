package com.sidework.project.application.port.in;


import com.sidework.project.domain.Project;

import java.util.List;

public interface ProjectQueryUseCase {
    Project queryById(Long projectId);
    List<Project> queryByUserId(Long userId);
}
