package com.sidework.project.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUser {
    private Long id;

    private Long projectId;

    private Long userId;

    private ApplyStatus status;

    private ProjectRole role;

    public static ProjectUser create(Long userId, Long projectId, ApplyStatus status, ProjectRole role) {
        return ProjectUser.builder()
                .userId(userId)
                .projectId(projectId)
                .status(status)
                .role(role)
                .build();
    }
}
