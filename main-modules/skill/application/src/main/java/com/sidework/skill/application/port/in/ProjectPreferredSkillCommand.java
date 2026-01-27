package com.sidework.skill.application.port.in;

import java.util.List;

public record ProjectPreferredSkillCommand(
        List<Long> skillIds
) {
}
