package com.sidework.profile.application.port.in;

import java.time.LocalDate;
import java.util.List;

import com.sidework.profile.domain.PortfolioType;
import com.sidework.profile.domain.SchoolStateType;
import com.sidework.profile.domain.SkillProficiencyType;

public record ProfileUpdateCommand(
	List<SchoolUpdateRequest> schools,
	List<PortfolioUpdateRequest> portfolios,
	List<SkillUpdateRequest> skills,
	List<Long> roleIds,
	String selfIntroduction,
	String residence
) {
	public record PortfolioUpdateRequest(
		Long portfolioId,
		PortfolioType type,
		LocalDate startDate,
		LocalDate endDate,
		String content
	) {}
	public record SchoolUpdateRequest(
		Long schoolId,
		SchoolStateType state,
		String major,
		LocalDate startDate,
		LocalDate endDate
	)
	{}
	public record SkillUpdateRequest(
		Long skillId,
		SkillProficiencyType proficiency
	)
	{}

}
