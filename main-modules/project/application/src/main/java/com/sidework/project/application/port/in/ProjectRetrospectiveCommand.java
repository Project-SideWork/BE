package com.sidework.project.application.port.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ProjectRetrospectiveCommand(
	@NotBlank String roleDescription,
	@NotBlank String strengths,
	@NotBlank String regrets,
	@NotBlank String learnings
) {}
