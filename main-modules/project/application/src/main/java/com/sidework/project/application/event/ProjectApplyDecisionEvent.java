package com.sidework.project.application.event;

public record ProjectApplyDecisionEvent(
	Long projectId,
	Long applicantUserId,
	String projectTitle,
	String projectRole,
	boolean approved
) {
}
