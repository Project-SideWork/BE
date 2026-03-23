package com.sidework.project.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectUserReviewCommand(
	@NotNull Long revieweeUserId,
	@NotNull @Min(1) @Max(5) Integer responsibility,
	@NotNull @Min(1) @Max(5) Integer communication,
	@NotNull @Min(1) @Max(5) Integer collaboration,
	@NotNull @Min(1) @Max(5) Integer problemSolving,
	@Size(max = 1000) String comment
) {
}
