package com.sidework.project.application.adapter;

import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.SkillLevel;

import java.time.LocalDate;
import java.util.List;

public record ProjectDetailResponse(
	Long id,
	String title,
	String description,
	LocalDate startDt,
	LocalDate endDt,
	MeetingType meetingType,
	ProjectStatus status,
	List<ProjectMemberResponse> teamMembers,
	List<RecruitPositionResponse> recruitPositions,
	List<String> requiredStacks,
	List<String> preferredStacks
) {
	public record ProjectMemberResponse(
		Long userId,
		Long profileId,
		ProjectRole role,
		ApplyStatus status
	) {
		public static ProjectMemberResponse of(Long userId, Long profileId, ProjectRole role, ApplyStatus status) {
			return new ProjectMemberResponse(userId, profileId, role, status);
		}
	}

	public record RecruitPositionResponse(
		ProjectRole role,
		Integer headCount,
		Integer currentCount,
		SkillLevel level
	) {
		public static RecruitPositionResponse of(ProjectRole role, Integer headCount, Integer currentCount, SkillLevel level) {
			return new RecruitPositionResponse(role, headCount, currentCount, level);
		}
	}
}

