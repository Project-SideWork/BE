package com.sidework.skill.application.port.in;

import com.sidework.skill.application.adapter.SkillSearchResponse;
import com.sidework.skill.domain.Skill;

import java.util.List;

public interface SkillSearchQueryUseCase {

	List<SkillSearchResponse> searchByName(String keyword);
}

