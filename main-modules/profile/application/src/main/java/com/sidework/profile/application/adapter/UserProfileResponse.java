package com.sidework.profile.application.adapter;

import com.sidework.profile.domain.PortfolioType;
import com.sidework.profile.domain.SchoolStateType;

import java.time.LocalDate;
import java.util.List;


//TODO: 프로젝트 정보 연동
public record UserProfileResponse(
	Long userId,
	String email,
	String name,
	String nickname,
	Integer age,
	String tel,
	Long profileId,
	List<RoleInfo> roles, //직군 정보
	List<SchoolInfo> schools, // 학교 장보
	List<SkillInfo> skills, //기술 스택
	List<PortfolioInfo> portfolios // 포폴 정보
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
		String skillName
	) {}

	public record PortfolioInfo(
		Long portfolioId,
		PortfolioType type,
		LocalDate startDate,
		LocalDate endDate,
		String content
	) {}
}
