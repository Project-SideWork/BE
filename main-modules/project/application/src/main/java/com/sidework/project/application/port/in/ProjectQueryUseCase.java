package com.sidework.project.application.port.in;


import com.sidework.project.domain.Project;

public interface ProjectQueryUseCase {
    Project queryById(Long projectId);
}
