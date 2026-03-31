package com.sidework.project.application.adapter;


public record MyProjectSummaryResponse(
	Long projectId,
	String title
) {
	public static MyProjectSummaryResponse create(Long projectId, String title) {
		return new MyProjectSummaryResponse(projectId, title);
	}
}
