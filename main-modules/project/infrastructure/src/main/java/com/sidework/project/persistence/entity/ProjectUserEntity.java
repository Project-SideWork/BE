package com.sidework.project.persistence.entity;

import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.ProjectRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

    @Enumerated(EnumType.STRING)
    private ProjectRole role;
}
