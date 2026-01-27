package com.sidework.skill.persistence.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class SkillNotFoundException extends GlobalException {
    public SkillNotFoundException(Long id) {
        super(ErrorStatus.SKILL_NOT_FOUND.withDetail("스킬 ID: " + id + "를 찾을 수 없습니다."));
    }
}

