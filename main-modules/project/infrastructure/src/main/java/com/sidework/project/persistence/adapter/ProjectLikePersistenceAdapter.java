package com.sidework.project.persistence.adapter;

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
}
