package com.sidework.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRecruitPosition {

	private Long id;
	private Long projectId;
	private ProjectRole role;
	private Integer headCount;
	private Integer currentCount;
	private SkillLevel level;

	public static ProjectRecruitPosition create(Long projectId, ProjectRole role, Integer headCount, SkillLevel level) {
		return ProjectRecruitPosition.builder()
			.projectId(projectId)
			.role(role)
			.headCount(headCount)
			.level(level)
			.currentCount(0)
			.build();
	}
}
