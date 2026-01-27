package com.sidework.skill.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPreferredSkill {
    private Long id;

    private Long projectId;

    private Long skillId;
}
