package com.sidework.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSkill
{
	private Long id;
	private Long profileId;
	private Long skillId;

	public static ProfileSkill create(Long profileId, Long skillId) {
		return ProfileSkill.builder()
			.profileId(profileId)
			.skillId(skillId)
			.build();
	}
}
