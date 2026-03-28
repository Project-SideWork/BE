package com.sidework.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserReviewStat {

	private Long userId;

	private double ratingScore;

	private long ratingCount;

	public static ProjectUserReviewStat create(Long userId, double ratingScore, long ratingCount) {
		return ProjectUserReviewStat.builder()
			.userId(userId)
			.ratingCount(ratingCount)
			.ratingScore(ratingScore)
			.build();
	}
}

