package com.sidework.skill.application.service;

import com.sidework.skill.domain.ProjectPreferredSkill;

import java.util.List;

public record PreferredSkillChangeSet(
        List<ProjectPreferredSkill> toAdd,
        List<ProjectPreferredSkill> toRemove
) {
}
