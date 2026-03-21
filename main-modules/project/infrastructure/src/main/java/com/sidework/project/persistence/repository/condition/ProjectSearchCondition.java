package com.sidework.project.persistence.repository.condition;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectSearchCondition {

	private String keyword;
	private List<Long> skillIds;
	private Long skillCount;
	private List<Long> projectIds;

}
