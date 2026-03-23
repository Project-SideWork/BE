package com.sidework.project.application.port.out;

import java.util.List;

import com.sidework.project.domain.ProjectUserReview;

public interface ProjectUserReviewOutPort {
	boolean exists(Long projectId, Long reviewerUserId, Long revieweeUserId);
	void saveAll(List<ProjectUserReview> review);

}
