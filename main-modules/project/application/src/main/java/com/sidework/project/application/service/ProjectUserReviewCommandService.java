package com.sidework.project.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sidework.project.application.dto.ProjectUserReviewCommand;
import com.sidework.project.application.exception.ProjectNotFinishedException;
import com.sidework.project.application.exception.ProjectSelfReviewNotAllowedException;
import com.sidework.project.application.exception.ProjectUserAlreadyReviewedException;
import com.sidework.project.application.exception.ProjectUserNotAcceptedException;
import com.sidework.project.application.exception.ProjectUserNotFoundException;
import com.sidework.project.application.port.in.ProjectUserReviewCommandUseCase;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.application.port.out.ProjectUserOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewOutPort;
import com.sidework.project.application.port.out.ProjectUserReviewStatOutPort;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.domain.ProjectUser;
import com.sidework.project.domain.ProjectUserReview;
import com.sidework.project.domain.ProjectUserReviewStat;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ProjectUserReviewCommandService implements ProjectUserReviewCommandUseCase {

	private final ProjectOutPort projectOutPort;
	private final ProjectUserOutPort projectUserOutPort;
	private final ProjectUserReviewOutPort projectUserReviewOutPort;
	private final ProjectUserReviewStatOutPort projectUserReviewStatOutPort;

	@Override
	public void create(Long reviewerUserId, Long projectId, ProjectUserReviewCommand command) {
		validateProject(projectId);
		validateReviewer(projectId, reviewerUserId);

		Map<Long, ProjectUser> memberMap = loadMemberMap(projectId);
		Set<Long> revieweeUserIds = extractRevieweeUserIds(command);

		validateReviewees(projectId, reviewerUserId, revieweeUserIds, memberMap);

		List<ProjectUserReview> reviews = command.reviews().stream()
			.map(review -> ProjectUserReview.create(
				projectId,
				reviewerUserId,
				review.revieweeUserId(),
				review.responsibility(),
				review.communication(),
				review.collaboration(),
				review.problemSolving(),
				review.comment()
			))
			.toList();

		try {
			projectUserReviewOutPort.saveAll(reviews);
		} catch (DataIntegrityViolationException e) {
			throw new ProjectUserAlreadyReviewedException();
		}
		saveReviewStats(reviews);
	}

	private void validateProject(Long projectId) {
		Project project = projectOutPort.findById(projectId);


		if (project == null || project.getStatus() != ProjectStatus.FINISHED) {
			throw new ProjectNotFinishedException(projectId);
		}
	}

	private void validateReviewer(Long projectId, Long reviewerUserId) {
		projectUserOutPort.findByProjectIdAndUserId(projectId, reviewerUserId)
			.orElseThrow(() -> new ProjectUserNotFoundException(reviewerUserId));

		boolean hasAccepted = projectUserOutPort.findAcceptedByProjectIdAndUserId(projectId, reviewerUserId)
			.isPresent();
		if (!hasAccepted) {
			throw new ProjectUserNotAcceptedException(reviewerUserId);
		}
	}

	private Map<Long, ProjectUser> loadMemberMap(Long projectId) {
		return projectUserOutPort.findAllByProjectId(projectId).stream()
			.collect(Collectors.toMap(ProjectUser::getUserId, Function.identity(), (a, b) -> a));
	}

	private Set<Long> extractRevieweeUserIds(ProjectUserReviewCommand command) {
		return command.reviews().stream()
			.map(ProjectUserReviewCommand.Review::revieweeUserId)
			.collect(Collectors.toSet());
	}

	private void validateReviewees(
		Long projectId,
		Long reviewerUserId,
		Set<Long> revieweeUserIds,
		Map<Long, ProjectUser> memberMap
	) {
		for (Long revieweeUserId : revieweeUserIds) {
			validateNotSelf(reviewerUserId, revieweeUserId);
			validateRevieweeAccepted(projectId, revieweeUserId, memberMap);
		}
	}

	private void validateNotSelf(Long reviewerUserId, Long revieweeUserId) {

		if (reviewerUserId.equals(revieweeUserId)) {
			throw new ProjectSelfReviewNotAllowedException();
		}
	}

	private void validateRevieweeAccepted(Long projectId, Long revieweeUserId, Map<Long, ProjectUser> memberMap) {
		ProjectUser reviewee = memberMap.get(revieweeUserId);
		if (reviewee == null) {
			throw new ProjectUserNotFoundException(projectId);
		}
		if (reviewee.getStatus() != ApplyStatus.ACCEPTED) {
			throw new ProjectUserNotAcceptedException(revieweeUserId);
		}
	}

	private void saveReviewStats(List<ProjectUserReview> reviews) {

		Map<Long, ProjectUserReviewStat> deltaByUserId = new HashMap<>();

		for (ProjectUserReview review : reviews) {
			Long userId = review.getRevieweeUserId();
			double score = calculateAverageScore(review);

			deltaByUserId.put(
				userId,
				ProjectUserReviewStat.create(userId, score, 1L)
			);
		}

		projectUserReviewStatOutPort.addAllReviewStats(
			new ArrayList<>(deltaByUserId.values())
		);
	}

	private double calculateAverageScore(ProjectUserReview review) {
		int totalScore =
			review.getResponsibility()
				+ review.getCommunication()
				+ review.getCollaboration()
				+ review.getProblemSolving();

		return totalScore / 4.0;
	}


}
