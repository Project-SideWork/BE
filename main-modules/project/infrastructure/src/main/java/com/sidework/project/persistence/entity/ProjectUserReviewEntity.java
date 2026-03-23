package com.sidework.project.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "project_user_reviews",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_project_user_review",
			columnNames = {"project_id", "reviewer_user_id", "reviewee_user_id"}
		)
	}
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserReviewEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@Column(name = "reviewer_user_id", nullable = false)
	private Long reviewerUserId;

	@Column(name = "reviewee_user_id", nullable = false)
	private Long revieweeUserId;

	@Column(name = "responsibility", nullable = false)
	private Integer responsibility;

	@Column(name = "communication", nullable = false)
	private Integer communication;

	@Column(name = "collaboration", nullable = false)
	private Integer collaboration;

	@Column(name = "problem_solving", nullable = false)
	private Integer problemSolving;

	@Column(name = "comment", length = 1000)
	private String comment;
}
