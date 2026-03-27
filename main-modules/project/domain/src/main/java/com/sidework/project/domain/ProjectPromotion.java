package com.sidework.project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPromotion {

	private Long id;
	private Long projectId;
	private String description;
	private String demoURL;
}
