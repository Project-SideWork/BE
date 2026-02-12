package com.sidework.project.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.application.port.out.ProjectRecruitPositionOutPort;
import com.sidework.project.domain.ProjectRecruitPosition;
import com.sidework.project.persistence.entity.ProjectRecruitPositionEntity;
import com.sidework.project.persistence.mapper.ProjectRecruitPositionMapper;
import com.sidework.project.persistence.repository.ProjectRecruitPositionJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectRecruitPositionPersistenceAdapter implements ProjectRecruitPositionOutPort {

	private final ProjectRecruitPositionJpaRepository repository;
	private final ProjectRecruitPositionMapper mapper;

	@Override
	public void saveAll(Long projectId, List<RecruitPosition> positions) {
		if (positions == null || positions.isEmpty()) {
			return;
		}
		List<ProjectRecruitPosition> domains = positions.stream()
			.map(position -> ProjectRecruitPosition.create(projectId, position.role(), position.headCount(), position.level()))
			.toList();
		List<ProjectRecruitPositionEntity> entities = domains.stream()
			.map(mapper::toEntity)
			.toList();
		repository.saveAll(entities);
	}

	@Override
	public void deleteByProjectId(Long projectId) {
		repository.deleteByProjectId(projectId);
	}

	@Override
	public List<ProjectRecruitPosition> getProjectRecruitPositions(Long projectId) {
		return repository.findByProjectId(projectId)
			.stream().map(mapper::toDomain).toList();

	}
}
