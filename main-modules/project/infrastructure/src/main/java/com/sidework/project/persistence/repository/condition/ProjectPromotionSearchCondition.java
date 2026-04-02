package com.sidework.project.persistence.repository.condition;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectPromotionSearchCondition {
	private String keyword;
	private List<Long> skillIds;
	private Long skillCount;
}
