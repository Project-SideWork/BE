package com.sidework.project.application.port.in;

public interface ProjectCommandUseCase {
    void create(Long userId, ProjectCommand command);
    void update(Long userId, Long projectId, ProjectCommand command);
    void delete(Long userId, Long projectId);
}
