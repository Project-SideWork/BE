package com.sidework.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileLike {

	private Long userId;
	private Long profileId;

	public static ProfileLike create(Long userId, Long profileId) {
		return ProfileLike.builder()
			.userId(userId)
			.profileId(profileId)
			.build();
	}
}
