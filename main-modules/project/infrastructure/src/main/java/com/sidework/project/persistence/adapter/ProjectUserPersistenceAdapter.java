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
import java.util.Optional;

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
    public List<Long> queryAllProjectIds(Long userId) {
        return repo.findAllIdsByUserId(userId);
    }

    @Override
    public Optional<ProjectUser> findByProjectIdAndUserId(Long projectId, Long userId) {
        return repo.findFirstByProjectIdAndUserId(projectId, userId)
            .map(mapper::toDomain);
    }
}
