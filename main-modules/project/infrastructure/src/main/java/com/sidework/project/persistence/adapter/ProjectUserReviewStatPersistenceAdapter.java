package com.sidework.project.persistence.adapter;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.sidework.project.application.exception.ProjectUserReviewStatNotFoundException;
import com.sidework.project.application.port.out.ProjectUserReviewStatOutPort;
import com.sidework.project.domain.ProjectUserReviewStat;
import com.sidework.project.persistence.entity.ProjectUserReviewStatEntity;
import com.sidework.project.persistence.mapper.ProjectUserReviewStatMapper;
import com.sidework.project.persistence.repository.ProjectUserReviewStatJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectUserReviewStatPersistenceAdapter implements ProjectUserReviewStatOutPort {

	private final ProjectUserReviewStatJpaRepository repository;
	private final ProjectUserReviewStatMapper mapper;

	@Override
	public void addAllReviewStats(List<ProjectUserReviewStat> stats) {
		for (ProjectUserReviewStat stat : stats) {

			int updated = repository.incrementStat(
				stat.getUserId(),
				stat.getRatingScore(),
				stat.getRatingCount()
			);

			if (updated == 0) {
				try {
					repository.save(
						mapper.toEntity(stat)
					);
				} catch (DataIntegrityViolationException e) {
					repository.incrementStat(
						stat.getUserId(),
						stat.getRatingScore(),
						stat.getRatingCount()
					);
				}
			}
		}

	}

	@Override
	public List<ProjectUserReviewStat> getAllReviewStatsByUserIds(List<Long> userIds) {
		return repository.findAllByUserIdIn(userIds).stream()
			.map(mapper::toDomain)
			.toList();
	}

	@Override
	public ProjectUserReviewStat getReviewStatByUserId(Long userId) {
		ProjectUserReviewStatEntity entity = repository.findByUserId(userId)
			.orElse(null);
		return mapper.toDomain(entity);
	}
}

