package com.sidework.project.persistence.adapter;

import com.sidework.project.application.port.out.ProjectRetrospectiveOutPort;
import com.sidework.project.domain.ProjectRetrospective;
import com.sidework.project.persistence.mapper.ProjectRetrospectiveMapper;
import com.sidework.project.persistence.repository.ProjectRetrospectiveJpaRepository;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectRetrospectivePersistenceAdapter implements ProjectRetrospectiveOutPort {

	private final ProjectRetrospectiveJpaRepository repo;
	private final ProjectRetrospectiveMapper mapper;

	@Override
	public Long save(ProjectRetrospective retrospective) {
		return repo.save(mapper.toEntity(retrospective)).getId();
	}

	@Override
	public boolean existsByProjectIdAndUserId(Long projectId, Long userId) {
		return repo.existsByProjectIdAndUserId(projectId, userId);
	}
}
