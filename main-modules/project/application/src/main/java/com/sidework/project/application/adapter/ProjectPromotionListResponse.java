package com.sidework.project.application.adapter;

import java.util.List;

public record ProjectPromotionListResponse(
	Long projectId,
	String title,
	String description,
	List<String> usedStacks
) {
}
