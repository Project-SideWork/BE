package com.sidework.profile.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPortfolio
{
	private Long id;
	private Long profileId;
	private Long portfolioId;

	public static ProjectPortfolio create(Long profileId, Long portfolioId) {
		return ProjectPortfolio.builder()
			.profileId(profileId)
			.portfolioId(portfolioId)
			.build();
	}
}
