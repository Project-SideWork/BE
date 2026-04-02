package com.sidework.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPromotion {

	private Long id;
	private Long projectId;
	private Long userId;
	private String description;
	private String demoUrl;

	public static ProjectPromotion create(Long projectId, Long userId, String description, String demoUrl ) {
		return ProjectPromotion.builder()
			.projectId(projectId)
			.userId(userId)
			.description(description)
			.demoUrl(demoUrl)
			.build();
	}

	public void update(String description, String demoUrl)
	{
		this.description = description;
		this.demoUrl = demoUrl;
	}
}
