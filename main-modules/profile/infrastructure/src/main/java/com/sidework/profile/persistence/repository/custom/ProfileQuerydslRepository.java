package com.sidework.profile.persistence.repository.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sidework.profile.persistence.entity.ProfileEntity;

public interface ProfileQuerydslRepository {
	Page<ProfileEntity> searchProfilesBySkillIds(List<Long> skillIds, Pageable pageable);
	Page<ProfileEntity> searchLikedProfilesBySkillIds(Long userId, List<Long> skillIds, Pageable pageable);
}

