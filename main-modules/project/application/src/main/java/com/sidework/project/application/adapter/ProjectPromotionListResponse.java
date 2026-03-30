package com.sidework.project.application.adapter;

import java.util.List;

public record ProjectPromotionListResponse(
	Long promotionId,
	Long projectId,
	String title,
	String description,
	List<String> usedStacks
) {
}
