package com.sidework.project.application.dto;

public record ProjectPromotionListRow(
	Long projectId,
	String title,
	String promotionDescription,
	Long promotionId
) {}
