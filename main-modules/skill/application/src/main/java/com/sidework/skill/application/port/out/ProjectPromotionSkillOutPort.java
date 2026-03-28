package com.sidework.skill.application.port.out;

import java.util.List;

import com.sidework.skill.domain.ProjectPromotionSkill;

public interface ProjectPromotionSkillOutPort {
	void saveAll(List<ProjectPromotionSkill> domains);
}
