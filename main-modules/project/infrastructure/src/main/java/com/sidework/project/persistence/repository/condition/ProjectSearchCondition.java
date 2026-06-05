package com.sidework.project.persistence.repository.condition;

import com.sidework.project.domain.ProjectRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectSearchCondition {
	private String keyword;
	private List<Long> skillIds;
    private List<ProjectRole> projectRoles;
	private Long skillCount;
	private List<Long> projectIds;
}
