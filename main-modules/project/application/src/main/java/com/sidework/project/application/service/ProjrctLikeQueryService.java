package com.sidework.project.application.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.project.application.port.in.ProjectLikeQueryUseCase;
import com.sidework.project.application.port.out.ProjectLikeOutPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjrctLikeQueryService implements ProjectLikeQueryUseCase {

	private final ProjectLikeOutPort projectLikeOutPort;

	@Override
	public Map<Long, Boolean> isLikedByProjectIds(Long userId, List<Long> projectIds) {
		return projectLikeOutPort.getLikes(userId, projectIds);
	}
}
