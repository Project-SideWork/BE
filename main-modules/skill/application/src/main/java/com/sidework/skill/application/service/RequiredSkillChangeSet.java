package com.sidework.skill.application.service;

import com.sidework.skill.domain.ProjectRequiredSkill;

import java.util.List;

public record RequiredSkillChangeSet(
        List<ProjectRequiredSkill> toAdd,
        List<Long> toRemoveIds
) {}
