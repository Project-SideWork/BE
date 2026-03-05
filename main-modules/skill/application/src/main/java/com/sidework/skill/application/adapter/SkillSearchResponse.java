package com.sidework.skill.application.adapter;

import com.sidework.skill.domain.Skill;

public record SkillSearchResponse(
	Long skillId,
	String skillName
) {
	public static SkillSearchResponse of(Skill skill) {
		return new SkillSearchResponse(skill.getId(), skill.getName());
	}
}
