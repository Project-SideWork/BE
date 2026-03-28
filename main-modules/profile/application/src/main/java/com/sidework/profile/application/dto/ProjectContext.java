package com.sidework.profile.application.dto;

import java.util.List;
import java.util.Map;

import com.sidework.project.application.dto.ProjectUserReviewSummary;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRole;

public record ProjectContext(
	List<Project> projects,
	List<Long> projectIds,
	Map<Long, String> projectIdToTitle,
	List<ProjectUserReviewSummary> reviews,
	Map<Long, List<String>> skillNamesByProjectId,
	Map<Long, List<ProjectRole>> rolesByProjectId
) {
}
