package com.sidework.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLike {
	private Long userId;
	private Long projectId;

	public static ProjectLike create(Long userId, Long projectId) {
		return ProjectLike.builder()
			.userId(userId)
			.projectId(projectId)
			.build();
	}
}
