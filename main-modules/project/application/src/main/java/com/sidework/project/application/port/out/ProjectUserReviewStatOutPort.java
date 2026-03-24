package com.sidework.project.application.port.out;

import java.util.List;

import com.sidework.project.domain.ProjectUserReviewStat;

public interface ProjectUserReviewStatOutPort {
	void addAllReviewStats(List<ProjectUserReviewStat> stats);
	List<ProjectUserReviewStat> getAllReviewStatsByUserIds(List<Long> userIds);
	ProjectUserReviewStat getReviewStatByUserId(Long userId);
}

