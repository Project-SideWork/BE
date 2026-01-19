package com.sidework.project.application.port.in;

public interface ProjectCommandUseCase {
    void create(ProjectCommand command);
    void update(Long projectId, ProjectCommand command);
    void delete(Long userId, Long projectId);
}
