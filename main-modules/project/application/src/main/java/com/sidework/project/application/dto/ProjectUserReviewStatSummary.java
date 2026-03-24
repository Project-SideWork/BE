package com.sidework.project.application.dto;

import com.sidework.project.domain.ProjectUserReviewStat;

public record ProjectUserReviewStatSummary(
	Double score,
	Long count
) {
	public static ProjectUserReviewStatSummary of(ProjectUserReviewStat stat) {
		return new ProjectUserReviewStatSummary(
			stat.getRatingScore(),
			stat.getRatingCount()
		);
	}
}
