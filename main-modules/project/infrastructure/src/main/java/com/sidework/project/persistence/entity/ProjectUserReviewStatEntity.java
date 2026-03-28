package com.sidework.project.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_user_review_stats")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserReviewStatEntity {

	@Id
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "rating_score", nullable = false)
	private Double ratingScore;

	@Column(name = "rating_count", nullable = false)
	private Long ratingCount;
}

