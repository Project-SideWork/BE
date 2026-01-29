package com.sidework.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRole {
    private Long id;
    private Long profileId;
    private Long roleId;

	public static ProfileRole create(Long profileId, Long roleId) {
		return ProfileRole.builder()
			.profileId(profileId)
			.roleId(roleId)
			.build();
	}
}
