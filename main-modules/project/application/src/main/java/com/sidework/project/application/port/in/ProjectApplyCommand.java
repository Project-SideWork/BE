package com.sidework.project.application.port.in;

import com.sidework.project.domain.ProjectRole;

import jakarta.validation.constraints.NotNull;

public record ProjectApplyCommand(
	@NotNull Long profileId,
	@NotNull ProjectRole role
	) {
}
