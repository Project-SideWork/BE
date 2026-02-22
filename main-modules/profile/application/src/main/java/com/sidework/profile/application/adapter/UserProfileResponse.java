package com.sidework.profile.application.adapter;

import com.sidework.profile.domain.PortfolioType;
import com.sidework.profile.domain.SchoolStateType;
import com.sidework.profile.domain.SkillProficiencyType;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;

import java.time.LocalDate;
import java.util.List;


public record UserProfileResponse(
	Long userId,
	String email,
	String name,
	String nickname,
	Integer age,
	String tel,
	Long profileId,
	String selfIntroduction,
	String residence,
	Integer projectCounts,
	List<RoleInfo> roles, //직군 정보
	List<SchoolInfo> schools, // 학교 장보
	List<SkillInfo> skills, //기술 스택
	List<PortfolioInfo> portfolios, // 포폴 정보
	List<ProjectInfo> projects // 프로젝트 정보
) {
	public record RoleInfo(
		Long roleId,
		String roleName
	) {}

	public record SchoolInfo(
		Long schoolId,
		String schoolName,
		String address,
		SchoolStateType state,
		String major,
		LocalDate startDate,
		LocalDate endDate
	) {}

	public record SkillInfo(
		Long skillId,
		String skillName,
		SkillProficiencyType proficiency
	) {}

	public record PortfolioInfo(
		Long portfolioId,
		PortfolioType type,
		LocalDate startDate,
		LocalDate endDate,
		String content,
		String organizationName
	) {}
	public record ProjectInfo(
		Long projectId,
		String title,
		String description,
		LocalDate startDate,
		LocalDate endDate,
		MeetingType meetingType,
		ProjectStatus status,
		List<String> projectStacks,
		List<ProjectRole> role
	) {}
}
