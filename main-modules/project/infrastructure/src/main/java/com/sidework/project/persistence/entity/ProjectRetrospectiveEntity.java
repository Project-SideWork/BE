package com.sidework.project.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_retrospectives")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRetrospectiveEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "role_description", columnDefinition = "TEXT", nullable = false)
	private String roleDescription;

	@Column(name = "strengths", columnDefinition = "TEXT", nullable = false)
	private String strengths;

	@Column(name = "regrets", columnDefinition = "TEXT", nullable = false)
	private String regrets;

	@Column(name = "learnings", columnDefinition = "TEXT", nullable = false)
	private String learnings;

}
