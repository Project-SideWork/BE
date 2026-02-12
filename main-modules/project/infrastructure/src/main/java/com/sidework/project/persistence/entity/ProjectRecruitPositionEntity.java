package com.sidework.project.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.SkillLevel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_recruit_positions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRecruitPositionEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProjectRole role;

	@Column(name = "head_count", nullable = false)
	private Integer headCount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SkillLevel level;
}
