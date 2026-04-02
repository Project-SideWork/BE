package com.sidework.skill.application.service;

import java.util.List;

import com.sidework.skill.domain.ProjectPromotionSkill;

public record PromotionSkillChangeSet(
	List<ProjectPromotionSkill> toAdd,
	List<Long> toRemoveIds
) {}
