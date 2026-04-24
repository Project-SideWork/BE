package com.sidework.profile.application.adapter;

import java.util.List;

import com.sidework.profile.domain.SkillProficiencyType;

public record UserProfileListResponse(
	Long profileId,
	Long userId,
	String name,
	String description,
	List<SkillInfo> skills,
	boolean liked,
	Double score

) {
	public record SkillInfo(
		Long skillId,
		String skillName,
		SkillProficiencyType proficiency
	) {}
}
