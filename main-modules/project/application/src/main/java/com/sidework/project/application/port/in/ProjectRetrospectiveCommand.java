package com.sidework.project.application.port.in;

import jakarta.validation.constraints.NotEmpty;

public record ProjectRetrospectiveCommand(
	@NotEmpty String roleDescription,
	@NotEmpty String strengths,
	@NotEmpty String improvements
) {}
