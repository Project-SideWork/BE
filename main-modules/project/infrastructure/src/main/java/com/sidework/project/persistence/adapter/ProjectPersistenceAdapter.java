package com.sidework.project.persistence.adapter;

import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.domain.Project;
import com.sidework.project.persistence.entity.ProjectEntity;
import com.sidework.project.persistence.exception.ProjectNotFoundException;
import com.sidework.project.persistence.mapper.ProjectMapper;
import com.sidework.project.persistence.repository.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectPersistenceAdapter implements ProjectOutPort {
    private final ProjectJpaRepository repo;
    private final ProjectMapper mapper;

    @Override
    public void save(Project project) {
        repo.save(mapper.toEntity(project));
    }

    @Override
    public Project findById(Long id) {
        ProjectEntity entity = repo.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
        return mapper.toDomain(entity);
    }
}
