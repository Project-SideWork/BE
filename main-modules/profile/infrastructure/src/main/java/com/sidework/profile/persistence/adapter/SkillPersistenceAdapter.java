package com.sidework.profile.persistence.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sidework.profile.application.port.out.SkillOutPort;
import com.sidework.profile.domain.Skill;
import com.sidework.profile.persistence.entity.SkillEntity;
import com.sidework.profile.persistence.exception.SkillNotFoundException;
import com.sidework.profile.persistence.mapper.SkillMapper;
import com.sidework.profile.persistence.repository.SkillJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SkillPersistenceAdapter implements SkillOutPort {

	private final SkillJpaRepository skillRepository;
	private final SkillMapper skillMapper;

	@Override
	public Skill findById(Long id) {
		SkillEntity entity = skillRepository.findById(id)
			.orElseThrow(() -> new SkillNotFoundException(id));
		return skillMapper.toDomain(entity);
	}

	@Override
	public List<Skill> findByIdIn(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		List<SkillEntity> entities = skillRepository.findByIdIn(ids);
		return entities.stream()
			.map(skillMapper::toDomain)
			.collect(Collectors.toList());
	}
}
