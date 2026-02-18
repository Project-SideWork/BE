package com.sidework.project.application.adapter;

import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;

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
	List<RecruitPosition> recruitPositions,
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
}

