package com.sidework.project.persistence.adapter;

import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectUser;
import com.sidework.project.persistence.entity.ProjectUserEntity;
import com.sidework.project.persistence.mapper.ProjectUserMapper;
import com.sidework.project.persistence.repository.ProjectUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectUserPersistenceAdapter implements ProjectUserOutPort {
    private final ProjectUserJpaRepository repo;
    private final ProjectUserMapper mapper;

    @Override
    public void save(ProjectUser projectUser) {
        ProjectUserEntity entity = mapper.toEntity(projectUser);
        repo.save(entity);
    }

    @Override
    public List<ProjectRole> queryUserRolesByProject(Long userId, Long projectId) {
         return repo.findAllRolesByUserAndProject(userId, projectId);
    }

    @Override
    public Map<Long, List<ProjectRole>> queryUserRolesByProjects(Long userId, List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return Map.of();
        }
        List<ProjectUserEntity> entities = repo.findByUserIdAndProjectIdIn(userId, projectIds);
        return entities.stream()
            .collect(Collectors.groupingBy(
                ProjectUserEntity::getProjectId,
                Collectors.mapping(ProjectUserEntity::getRole, Collectors.toList())
            ));
    }

    @Override
    public List<Long> queryAllProjectIds(Long userId) {
        return repo.findAllIdsByUserId(userId);
    }

    @Override
    public Optional<ProjectUser> findByProjectIdAndUserId(Long projectId, Long userId) {
        return repo.findFirstByProjectIdAndUserId(projectId, userId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<ProjectUser> findByProjectIdAndUserIdAndRole(Long projectId, Long userId, ProjectRole role) {
        return repo.findFirstByProjectIdAndUserIdAndRole(projectId, userId, role)
            .map(mapper::toDomain);
    }

    @Override
    public List<ProjectUser> findAllByProjectId(Long projectId) {
        List<ProjectUserEntity> entities = repo.findAllByProjectId(projectId);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public Map<Long, Long> findOwnerUserIdByProjectIds(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> rows = repo.findOwnerProjectIdAndUserIdByProjectIdIn(projectIds, ProjectRole.OWNER);
        return rows.stream()
            .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1], (a, b) -> a));
    }
}
