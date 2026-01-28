package com.sidework.profile.persistence.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sidework.profile.application.port.in.ProfileUpdateCommand;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.domain.ProjectPortfolio;
import com.sidework.profile.persistence.entity.ProfileEntity;
import com.sidework.profile.persistence.entity.ProfileRoleEntity;
import com.sidework.profile.persistence.entity.ProfileSchoolEntity;
import com.sidework.profile.persistence.entity.ProfileSkillEntity;
import com.sidework.profile.persistence.entity.ProjectPortfolioEntity;
import com.sidework.profile.persistence.exception.ProfileNotFoundException;
import com.sidework.profile.persistence.mapper.ProfileMapper;
import com.sidework.profile.persistence.mapper.ProfileRoleMapper;
import com.sidework.profile.persistence.mapper.ProfileSchoolMapper;
import com.sidework.profile.persistence.mapper.ProfileSkillMapper;
import com.sidework.profile.persistence.mapper.ProjectPortfolioMapper;
import com.sidework.profile.persistence.repository.ProfileJpaRepository;
import com.sidework.profile.persistence.repository.ProfileRoleJpaRepository;
import com.sidework.profile.persistence.repository.ProfileSchoolJpaRepository;
import com.sidework.profile.persistence.repository.ProfileSkillJpaRepository;
import com.sidework.profile.persistence.repository.ProjectPortfolioJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfilePersistenceAdapter implements ProfileOutPort
{
	private final ProfileJpaRepository profileRepository;
	private final ProfileRoleJpaRepository profileRoleRepository;
	private final ProfileSchoolJpaRepository profileSchoolRepository;
	private final ProfileSkillJpaRepository profileSkillRepository;
	private final ProjectPortfolioJpaRepository projectPortfolioRepository;

	private final ProfileRoleMapper profileRoleMapper;
	private final ProfileMapper profileMapper;
	private final ProfileSchoolMapper profileSchoolMapper;
	private final ProfileSkillMapper profileSkillMapper;
	private final ProjectPortfolioMapper projectPortfolioMapper;

	@Override
	public boolean existsByUserId(Long userId) {
		return profileRepository.existsByUserId(userId);
	}

	@Override
	public Profile getProfileByUserId(Long userId) {
		ProfileEntity entity = profileRepository.findByUserId(userId)
			.orElse(null);
		return profileMapper.toDomain(entity);
	}

	@Override
	public List<ProfileRole> getProfileRoles(Long profileId) {
		List<ProfileRoleEntity> entities = profileRoleRepository.findByProfileId(profileId);
		return entities.stream()
			.map(profileRoleMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public List<ProfileSchool> getProfileSchools(Long profileId) {
		List<ProfileSchoolEntity> entities = profileSchoolRepository.findByProfileId(profileId);
		return entities.stream()
			.map(profileSchoolMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public List<ProfileSkill> getProfileSkills(Long profileId) {
		List<ProfileSkillEntity> entities = profileSkillRepository.findByProfileId(profileId);
		return entities.stream()
			.map(profileSkillMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public List<ProjectPortfolio> getProjectPortfolios(Long profileId) {
		List<ProjectPortfolioEntity> entities = projectPortfolioRepository.findByProfileId(profileId);
		return entities.stream()
			.map(projectPortfolioMapper::toDomain)
			.collect(Collectors.toList());
	}

	@Override
	public void saveProfileRoles(List<ProfileRole> profileRoles) {
		if (profileRoles == null || profileRoles.isEmpty()) {
			return;
		}
		List<ProfileRoleEntity> entities = profileRoles.stream()
			.map(profileRoleMapper::toEntity)
			.collect(Collectors.toList());
		profileRoleRepository.saveAll(entities);
	}

	@Override
	public void saveProfileSchools(List<ProfileSchool> profileSchools) {
		if (profileSchools == null || profileSchools.isEmpty()) {
			return;
		}
		List<ProfileSchoolEntity> entities = profileSchools.stream()
			.map(profileSchoolMapper::toEntity)
			.collect(Collectors.toList());
		profileSchoolRepository.saveAll(entities);
	}

	@Override
	public void saveProfileSkills(List<ProfileSkill> profileSkills) {
		if (profileSkills == null || profileSkills.isEmpty()) {
			return;
		}
		List<ProfileSkillEntity> entities = profileSkills.stream()
			.map(profileSkillMapper::toEntity)
			.collect(Collectors.toList());
		profileSkillRepository.saveAll(entities);
	}

	@Override
	public void saveProjectPortfolios(List<ProjectPortfolio> projectPortfolios) {
		if (projectPortfolios == null || projectPortfolios.isEmpty()) {
			return;
		}
		List<ProjectPortfolioEntity> entities = projectPortfolios.stream()
			.map(projectPortfolioMapper::toEntity)
			.collect(Collectors.toList());
		projectPortfolioRepository.saveAll(entities);
	}

	@Override
	public void deleteAllProfileRolesByProfileId(Long profileId) {
		if(profileId == null) return;
		profileRoleRepository.deleteAllByProfileId(profileId);
	}

	@Override
	public void deleteAllProfileSchoolsByProfileId(Long profileId) {
		if(profileId == null) return;
		profileSchoolRepository.deleteAllByProfileId(profileId);
	}

	@Override
	public void deleteAllProfileSkillsByProfileId(Long profileId) {
		if(profileId == null) return;
		profileSkillRepository.deleteAllByProfileId(profileId);
	}

	@Override
	public void deleteAllProjectPortfoliosByProfileId(Long profileId) {
		if(profileId == null) return;
		projectPortfolioRepository.deleteAllByProfileId(profileId);
	}
}
