package com.sidework.project.application.dto;

import com.sidework.project.domain.ProjectUserReview;

public record ProjectUserReviewSummary(
	Long projectId,
	String reviewer,
	String comment,
	Double score
) {
	public static ProjectUserReviewSummary of(
		ProjectUserReview review,
		String reviewerName,
		double score
	) {
		return new ProjectUserReviewSummary(
			review.getProjectId(),
			reviewerName,
			review.getComment(),
			score
		);
	}
}
