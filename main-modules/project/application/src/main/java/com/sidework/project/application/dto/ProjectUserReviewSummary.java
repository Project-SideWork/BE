package com.sidework.project.application.dto;

import com.sidework.project.domain.ProjectUserReview;

import java.time.LocalDate;

public record ProjectUserReviewSummary(
	Long projectId,
	String comment,
	Double score,
    LocalDate reviewDt
) {
	public static ProjectUserReviewSummary of(
		ProjectUserReview review,
		double score
	) {
		return new ProjectUserReviewSummary(
			review.getProjectId(),
			review.getComment(),
			score,review.getCreatedAt()
		);
	}
}
