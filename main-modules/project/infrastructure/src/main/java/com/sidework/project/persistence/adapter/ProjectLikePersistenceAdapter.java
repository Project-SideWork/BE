package com.sidework.project.persistence.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sidework.project.application.port.out.ProjectLikeOutPort;
import com.sidework.project.domain.ProjectLike;
import com.sidework.project.persistence.mapper.ProjectLikeMapper;
import com.sidework.project.persistence.repository.ProjectLikeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectLikePersistenceAdapter implements ProjectLikeOutPort {

	private final ProjectLikeRepository projectLikeRepository;
	private final ProjectLikeMapper projectLikeMapper;

	@Override
	public void like(ProjectLike like) {
		projectLikeRepository.save(projectLikeMapper.toEntity(like));
	}

	@Override
	public void unlike(ProjectLike like) {
		projectLikeRepository.deleteByUserIdAndProjectId(like.getUserId(), like.getProjectId());
	}

	@Override
	public boolean isLiked(Long userId, Long projectId) {
		return projectLikeRepository.existsByUserIdAndProjectId(userId, projectId);
	}

	@Override
	public Map<Long, Boolean> getLikes(Long userId, List<Long> projectIds) {
		if (projectIds == null || projectIds.isEmpty()) {
			return Map.of();
		}
		Set<Long> projectIdSet = new HashSet<>(
			projectLikeRepository.findProjectIdsByUserIdAndProjectIdIn(
				userId, projectIds)
		);

		return projectIdSet.stream()
			.collect(Collectors.toMap(id->id, projectIdSet::contains));
	}
}
