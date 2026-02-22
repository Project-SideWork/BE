package com.sidework.profile.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SkillProficiencyType {
	BEGINNER("초급"),
	INTERMEDIATE("중급"),
	ADVANCED("고급");

	private final String value;
}
