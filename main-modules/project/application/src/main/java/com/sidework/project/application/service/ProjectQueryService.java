package com.sidework.project.application.service;

import java.util.List;

import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryService implements ProjectQueryUseCase {
    private final ProjectOutPort projectRepository;
    private final ProjectUserOutPort projectUserRepository;

    @Override
    public Project queryById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public List<Project> queryByUserId(Long userId) {
        List<Long> projectIds = projectUserRepository.queryAllProjectIds(userId);
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of();
        }
        return projectIds.stream()
            .map(projectRepository::findById)
            .toList();
    }
}
