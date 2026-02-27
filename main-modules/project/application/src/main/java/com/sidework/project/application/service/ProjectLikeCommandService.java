package com.sidework.project.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.project.application.port.in.ProjectLikeCommandUseCase;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.application.port.out.ProjectLikeOutPort;
import com.sidework.project.domain.ProjectLike;
import com.sidework.user.application.port.in.UserQueryUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectLikeCommandService implements ProjectLikeCommandUseCase {

	private final ProjectLikeOutPort projectLikeRepository;

	private final ProjectQueryUseCase projectRepository;
	private final UserQueryUseCase userRepository;

	@Override
	public void like(Long userId, Long projectId) {
		projectRepository.queryById(projectId);
		userRepository.validateExists(userId);

		ProjectLike like = ProjectLike.create(userId, projectId);
		if(projectLikeRepository.isLiked(userId, projectId)) {
			projectLikeRepository.unlike(like);
		}
		else {
			projectLikeRepository.like(like);
		}
	}
}
