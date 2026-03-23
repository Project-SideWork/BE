package com.sidework.project.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.sidework.project.application.port.out.ProjectUserReviewOutPort;
import com.sidework.project.domain.ProjectUserReview;
import com.sidework.project.persistence.entity.ProjectUserReviewEntity;
import com.sidework.project.persistence.mapper.ProjectUserReviewMapper;
import com.sidework.project.persistence.repository.custom.ProjectUserReviewJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectUserReviewPersistenceAdapter implements ProjectUserReviewOutPort {

	private final ProjectUserReviewJpaRepository repository;
	private final ProjectUserReviewMapper mapper;

	@Override
	public boolean exists(Long projectId, Long reviewerUserId, Long revieweeUserId) {
		return repository.existsByProjectIdAndReviewerUserIdAndRevieweeUserId(
			projectId, reviewerUserId, revieweeUserId
		);

	}

	@Override
	public void saveAll(List<ProjectUserReview> reviews) {
		List<ProjectUserReviewEntity> entities = reviews.stream()
			.map(mapper::toEntity)
			.toList();
		repository.saveAll(entities);
	}
}
