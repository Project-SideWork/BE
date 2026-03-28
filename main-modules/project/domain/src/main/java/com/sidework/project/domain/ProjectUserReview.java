package com.sidework.project.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserReview {
	private Long id;
	private Long projectId;

	private Long reviewerUserId;//평가하는 사람
	private Long revieweeUserId;//평가받는 사람

	private Integer responsibility;
	private Integer communication;
	private Integer collaboration;
	private Integer problemSolving;

	private String comment;

	public static ProjectUserReview create(
		Long projectId,
		Long reviewerUserId,
		Long revieweeUserId,
		Integer responsibility,
		Integer communication,
		Integer collaboration,
		Integer problemSolving,
		String comment
	) {
		return ProjectUserReview.builder()
			.projectId(projectId)
			.reviewerUserId(reviewerUserId)
			.revieweeUserId(revieweeUserId)
			.responsibility(responsibility)
			.communication(communication)
			.collaboration(collaboration)
			.problemSolving(problemSolving)
			.comment(comment)
			.build();
	}
}
