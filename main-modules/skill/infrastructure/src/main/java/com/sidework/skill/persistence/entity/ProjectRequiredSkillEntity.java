package com.sidework.skill.persistence.entity;

import com.sidework.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_required_skills")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequiredSkillEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "skill_id", nullable = false)
    private Long skillId;
}
