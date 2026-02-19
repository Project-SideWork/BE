package com.sidework.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile
{
	private Long id;
	private Long userId;
	private String selfIntroduction;
	private String residence;

	public static Profile create(Long userId, String selfIntroduction) {
		return Profile.builder()
			.userId(userId)
			.selfIntroduction(selfIntroduction)
			.build();
	}

	public void updateSelfIntroduction(String selfIntroduction) {
		this.selfIntroduction = selfIntroduction;
	}

	public void updateResidence(String residence) {
		this.residence = residence;
	}

}
