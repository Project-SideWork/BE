package com.sidework.profile.persistence;

import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.domain.ProjectPortfolio;
import com.sidework.profile.persistence.adapter.ProfilePersistenceAdapter;
import com.sidework.profile.persistence.entity.ProfileEntity;
import com.sidework.profile.persistence.entity.ProfileRoleEntity;
import com.sidework.profile.persistence.entity.ProfileSchoolEntity;
import com.sidework.profile.persistence.entity.ProfileSkillEntity;
import com.sidework.profile.persistence.entity.ProjectPortfolioEntity;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilePersistenceAdapterTest {

	@Mock
	private ProfileJpaRepository profileRepository;

	@Mock
	private ProfileRoleJpaRepository profileRoleRepository;

	@Mock
	private ProfileSchoolJpaRepository profileSchoolRepository;

	@Mock
	private ProfileSkillJpaRepository profileSkillRepository;

	@Mock
	private ProjectPortfolioJpaRepository projectPortfolioRepository;

	private ProfileMapper profileMapper = Mappers.getMapper(ProfileMapper.class);
	private ProfileRoleMapper profileRoleMapper = Mappers.getMapper(ProfileRoleMapper.class);
	private ProfileSchoolMapper profileSchoolMapper = Mappers.getMapper(ProfileSchoolMapper.class);
	private ProfileSkillMapper profileSkillMapper = Mappers.getMapper(ProfileSkillMapper.class);
	private ProjectPortfolioMapper projectPortfolioMapper = Mappers.getMapper(ProjectPortfolioMapper.class);

	private ProfilePersistenceAdapter adapter;

	@BeforeEach
	void setUp() {
		adapter = new ProfilePersistenceAdapter(
			profileRepository,
			profileRoleRepository,
			profileSchoolRepository,
			profileSkillRepository,
			projectPortfolioRepository,
			profileRoleMapper,
			profileMapper,
			profileSchoolMapper,
			profileSkillMapper,
			projectPortfolioMapper
		);
	}

	@Test
	void existsByUserId는_프로필_존재_여부를_확인한다() {
		// given
		Long userId = 1L;
		when(profileRepository.existsByUserId(userId)).thenReturn(true);

		// when
		boolean exists = adapter.existsByUserId(userId);

		// then
		assertTrue(exists);
		verify(profileRepository).existsByUserId(userId);
	}

	@Test
	void getProfileByUserId는_사용자ID로_프로필을_조회해_도메인_객체로_변환한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		ProfileEntity entity = createProfileEntity(profileId, userId);
		when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(entity));

		// when
		Profile profile = adapter.getProfileByUserId(userId);

		// then
		assertNotNull(profile);
		assertEquals(profileId, profile.getId());
		assertEquals(userId, profile.getUserId());
		verify(profileRepository).findByUserId(userId);
	}

	@Test
	void getProfileByUserId는_프로필이_없으면_null을_반환한다() {
		// given
		Long userId = 1L;
		when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());

		// when
		Profile profile = adapter.getProfileByUserId(userId);

		// then
		assertNull(profile);
		verify(profileRepository).findByUserId(userId);
	}

	@Test
	void getProfileRoles는_프로필ID로_역할_목록을_조회한다() {
		// given
		Long profileId = 1L;
		List<ProfileRoleEntity> entities = createProfileRoleEntities(profileId);
		when(profileRoleRepository.findByProfileId(profileId)).thenReturn(entities);

		// when
		List<ProfileRole> roles = adapter.getProfileRoles(profileId);

		// then
		assertNotNull(roles);
		assertEquals(2, roles.size());
		verify(profileRoleRepository).findByProfileId(profileId);
	}

	@Test
	void getProfileSchools는_프로필ID로_학교_목록을_조회한다() {
		// given
		Long profileId = 1L;
		List<ProfileSchoolEntity> entities = createProfileSchoolEntities(profileId);
		when(profileSchoolRepository.findByProfileId(profileId)).thenReturn(entities);

		// when
		List<ProfileSchool> schools = adapter.getProfileSchools(profileId);

		// then
		assertNotNull(schools);
		assertEquals(1, schools.size());
		verify(profileSchoolRepository).findByProfileId(profileId);
	}

	@Test
	void getProfileSkills는_프로필ID로_스킬_목록을_조회한다() {
		// given
		Long profileId = 1L;
		List<ProfileSkillEntity> entities = createProfileSkillEntities(profileId);
		when(profileSkillRepository.findByProfileId(profileId)).thenReturn(entities);

		// when
		List<ProfileSkill> skills = adapter.getProfileSkills(profileId);

		// then
		assertNotNull(skills);
		assertEquals(2, skills.size());
		verify(profileSkillRepository).findByProfileId(profileId);
	}

	@Test
	void getProjectPortfolios는_프로필ID로_포트폴리오_목록을_조회한다() {
		// given
		Long profileId = 1L;
		List<ProjectPortfolioEntity> entities = createProjectPortfolioEntities(profileId);
		when(projectPortfolioRepository.findByProfileId(profileId)).thenReturn(entities);

		// when
		List<ProjectPortfolio> portfolios = adapter.getProjectPortfolios(profileId);

		// then
		assertNotNull(portfolios);
		assertEquals(2, portfolios.size());
		verify(projectPortfolioRepository).findByProfileId(profileId);
	}

	private ProfileEntity createProfileEntity(Long profileId, Long userId) {
		return ProfileEntity.builder()
			.id(profileId)
			.userId(userId)
			.build();
	}

	private List<ProfileRoleEntity> createProfileRoleEntities(Long profileId) {
		return List.of(
			ProfileRoleEntity.builder().id(1L).profileId(profileId).roleId(1L).build(),
			ProfileRoleEntity.builder().id(2L).profileId(profileId).roleId(2L).build()
		);
	}

	private List<ProfileSchoolEntity> createProfileSchoolEntities(Long profileId) {
		return List.of(
			ProfileSchoolEntity.builder()
				.id(1L)
				.profileId(profileId)
				.schoolId(1L)
				.build()
		);
	}

	private List<ProfileSkillEntity> createProfileSkillEntities(Long profileId) {
		return List.of(
			ProfileSkillEntity.builder().id(1L).profileId(profileId).skillId(1L).build(),
			ProfileSkillEntity.builder().id(2L).profileId(profileId).skillId(2L).build()
		);
	}

	private List<ProjectPortfolioEntity> createProjectPortfolioEntities(Long profileId) {
		return List.of(
			ProjectPortfolioEntity.builder().id(1L).profileId(profileId).portfolioId(1L).build(),
			ProjectPortfolioEntity.builder().id(2L).profileId(profileId).portfolioId(2L).build()
		);
	}
}

