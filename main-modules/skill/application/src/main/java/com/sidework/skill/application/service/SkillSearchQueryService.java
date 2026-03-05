package com.sidework.skill.application.service;

import com.sidework.skill.application.adapter.SkillSearchResponse;
import com.sidework.skill.application.port.in.SkillSearchQueryUseCase;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.Skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillSearchQueryService implements SkillSearchQueryUseCase {

	private final SkillOutPort skillOutPort;

	@Override
	public List<SkillSearchResponse> searchByName(String keyword) {
		return skillOutPort.searchByName(keyword).stream()
			.map(this::toSkillResponse)
			.toList();
	}

	private SkillSearchResponse toSkillResponse(Skill skill) {
		return SkillSearchResponse.of(skill);
	}
}


