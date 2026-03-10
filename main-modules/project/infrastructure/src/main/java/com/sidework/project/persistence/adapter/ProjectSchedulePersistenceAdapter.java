package com.sidework.project.persistence.adapter;

import com.sidework.project.application.port.out.ProjectScheduleOutPort;
import com.sidework.project.domain.ProjectSchedule;
import com.sidework.project.persistence.entity.ProjectScheduleEntity;
import com.sidework.project.persistence.mapper.ProjectScheduleMapper;
import com.sidework.project.persistence.repository.ProjectScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectSchedulePersistenceAdapter implements ProjectScheduleOutPort {
    private final ProjectScheduleMapper mapper;
    private final ProjectScheduleJpaRepository repo;

    @Override
    public void deleteAll(Long projectId) {
        repo.deleteAllByProjectId(projectId);
    }

    @Override
    public void saveAll(List<ProjectSchedule> schedules) {
        List<ProjectScheduleEntity> entities = schedules.stream().map(
                mapper::toEntity
        ).toList();
        repo.saveAll(entities);
    }
}
