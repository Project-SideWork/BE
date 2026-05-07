package com.sidework.project.application.port.out;

import java.util.List;

import com.sidework.project.domain.ProjectUserReview;
import org.springframework.data.domain.Pageable;

public interface ProjectUserReviewOutPort {
	boolean exists(Long projectId, Long reviewerUserId, Long revieweeUserId);
	void saveAll(List<ProjectUserReview> review);
	List<ProjectUserReview> getReviewsByUserIdAndProjectIds(Long userId, List<Long> projectIds);
	List<ProjectUserReview> getReviewsByUserId(Long userId, Pageable pageable);
    Long findReviewCountByUserId(Long userId);
    List<ProjectUserReview> pageReviewByUserId(Long userId, Pageable pageable);
}
