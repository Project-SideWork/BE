package com.sidework.project.persistence.adapter;

import com.sidework.project.application.dto.ProjectTitleDto;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.domain.Project;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.domain.ProjectRecruitPosition;
import com.sidework.project.persistence.entity.ProjectEntity;
import com.sidework.project.persistence.entity.ProjectRecruitPositionEntity;
import com.sidework.project.persistence.mapper.ProjectMapper;
import com.sidework.project.persistence.mapper.ProjectRecruitPositionMapper;
import com.sidework.project.persistence.repository.ProjectJpaRepository;
import com.sidework.project.persistence.repository.ProjectRecruitPositionJpaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectPersistenceAdapter implements ProjectOutPort {
    private final ProjectJpaRepository repo;
    private final ProjectRecruitPositionJpaRepository recruitPositionRepo;
    private final ProjectMapper mapper;
    private final ProjectRecruitPositionMapper recruitMapper;

    @Override
    public boolean existsById(Long projectId) {
          return repo.existsById(projectId);
    }

    @Override
    public Long save(Project project) {
        return repo.save(mapper.toEntity(project)).getId();
    }

    @Override
    public Project findById(Long id) {
        ProjectEntity entity = repo.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
        return mapper.toDomain(entity);
    }

    @Override
    public List<Project> findByIdIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return repo.findAllById(ids).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<ProjectTitleDto> findAllTitles(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return List.of();
        }
        return repo.findProjectionsByIds(projectIds);
    }

    @Override
    public Page<Project> findPage(Pageable pageable) {
        Page<ProjectEntity> entities = repo.findAll(pageable);
        return entities.map(mapper::toDomain);
    }

    @Override
    public Map<Long, List<ProjectRecruitPosition>> getProjectRecruitPositionsByProjectIds(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return Map.of();
        }
        List<ProjectRecruitPositionEntity> entities = recruitPositionRepo.findByProjectIdIn(projectIds);
        return entities.stream()
            .map(recruitMapper::toDomain)
            .collect(Collectors.groupingBy(ProjectRecruitPosition::getProjectId));
    }
}
