package com.sidework.project.application.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record ProjectPromotionCommand(
	@NotEmpty
	String description,

	@NotEmpty
	List<Long> usedSkillIds,

	String demoUrl
) {}
