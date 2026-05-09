package com.sidework.project.application.adapter;

import java.time.Instant;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.ProjectRole;

public record ProjectApplicantResponse(
	Long userId,
	String userName,
	Long profileId,
	ProjectRole role,
	ApplyStatus status,
	Double score,
	Instant createdAt
) {
	public static ProjectApplicantResponse of(Long userId, String userName, Long profileId, ProjectRole role, ApplyStatus status, Double score, Instant createdAt) {
		return new ProjectApplicantResponse(userId, userName, profileId, role, status, score, createdAt);
	}
}
