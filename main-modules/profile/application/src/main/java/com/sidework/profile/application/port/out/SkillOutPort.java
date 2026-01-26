package com.sidework.profile.application.port.out;

import java.util.List;

import com.sidework.profile.domain.Skill;

public interface SkillOutPort
{
	Skill findById(Long id);
	List<Skill> findByIdIn(List<Long> ids);
}
