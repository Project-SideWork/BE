package com.sidework.skill.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPromotionSkill {
	private Long id;

	private Long projectId;

	private Long userId;

	private Long promotionId;

	private Long skillId;
}
