package com.sidework.project.application.adapter;

import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.ProjectRole;

public record ProjectApplicantResponse(
	Long userId,
	Long profileId,
	ProjectRole role,
	ApplyStatus status,
	Double score
) {
	public static ProjectApplicantResponse of(Long userId, Long profileId, ProjectRole role, ApplyStatus status, Double score) {
		return new ProjectApplicantResponse(userId, profileId, role, status, score);
	}
}
