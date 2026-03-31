package com.sidework.project.application.adapter;

import java.util.List;

import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.ProjectRole;

public record ProjectPromotionDetailResponse(
	Long projectId,
	Long promotionId,
	String title,
	String description,
	MeetingType meetingType,
	List<String> usedStacks,
	String meetingPlace,
	Integer duration,
	List<ProjectMemberResponse> teamMembers

) {
	public record ProjectMemberResponse(
		Long userId,
		Long profileId,
		ProjectRole role,
		ApplyStatus status,
		Double score
	) {
		public static ProjectDetailResponse.ProjectMemberResponse of(Long userId, Long profileId, ProjectRole role, ApplyStatus status,Double score) {
			return new ProjectDetailResponse.ProjectMemberResponse(userId, profileId, role, status, score);
		}
	}
}
