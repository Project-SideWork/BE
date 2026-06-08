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
    String demoUrl,
	MeetingType meetingType,
	List<String> usedStacks,
	String meetingPlace,
	Integer duration,
	List<ProjectMemberResponse> teamMembers

) {
	public record ProjectMemberResponse(
		Long profileId,
		String nickname,
		ProjectRole role,
		Double score
	) {
		public static ProjectMemberResponse of(Long profileId, String nickname, ProjectRole role, Double score) {
			return new ProjectMemberResponse(profileId, nickname, role, score);
		}
	}
}
