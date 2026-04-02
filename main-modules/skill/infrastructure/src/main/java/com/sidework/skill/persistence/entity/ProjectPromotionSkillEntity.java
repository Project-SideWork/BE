package com.sidework.skill.persistence.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_promotion_skills")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPromotionSkillEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(name = "project_id", nullable = false)
	private Long projectId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "promotion_id", nullable = false)
	private Long promotionId;

	@Column(name = "skill_id", nullable = false)
	private Long skillId;
}
