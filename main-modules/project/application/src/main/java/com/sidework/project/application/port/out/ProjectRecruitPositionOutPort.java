package com.sidework.project.application.port.out;

import java.util.List;

import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.domain.ProjectRecruitPosition;

public interface ProjectRecruitPositionOutPort {

	void saveAll(Long projectId, List<RecruitPosition> positions);
	void deleteByProjectId(Long projectId);
	List<ProjectRecruitPosition> getProjectRecruitPositions(Long projectId);
}
