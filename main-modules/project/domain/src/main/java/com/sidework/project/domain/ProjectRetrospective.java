package com.sidework.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRetrospective {

	private Long id;
	private Long projectId;
	private Long userId;
	private String roleDescription;
	private String strengths;
	private String improvements;

	public static  ProjectRetrospective create(Long projectId, Long userId, String roleDescription, String strengths, String improvements) {
		return ProjectRetrospective.builder()
			.projectId(projectId)
			.userId(userId)
			.roleDescription(roleDescription)
			.strengths(strengths)
			.improvements(improvements)
			.build();
	}

}
