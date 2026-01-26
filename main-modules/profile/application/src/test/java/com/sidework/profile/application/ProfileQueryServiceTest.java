package com.sidework.profile.application;

import com.sidework.profile.application.adapter.UserProfileResponse;
import com.sidework.profile.application.port.out.PortfolioOutPort;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.application.port.out.RoleOutPort;
import com.sidework.profile.application.port.out.SchoolOutPort;
import com.sidework.profile.application.port.out.SkillOutPort;
import com.sidework.profile.application.service.ProfileQueryService;
import com.sidework.profile.domain.Portfolio;
import com.sidework.profile.domain.PortfolioType;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.domain.ProjectPortfolio;
import com.sidework.profile.domain.Role;
import com.sidework.profile.domain.School;
import com.sidework.profile.domain.SchoolStateType;
import com.sidework.profile.domain.Skill;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.user.application.port.in.UserQueryUseCase;
import com.sidework.user.domain.User;
import com.sidework.user.domain.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceTest {

	@Mock
	private ProfileOutPort profileRepository;

	@Mock
	private SchoolOutPort schoolRepository;

	@Mock
	private SkillOutPort skillRepository;

	@Mock
	private PortfolioOutPort portfolioRepository;

	@Mock
	private RoleOutPort roleRepository;

	@Mock
	private UserQueryUseCase userQueryUseCase;

	@Mock
	private ProjectQueryUseCase projectQueryUseCase;

	@InjectMocks
	private ProfileQueryService service;

	@Test
	void 프로필이_없을_때_기본_사용자_정보만_반환한다() {
		// given
		Long userId = 1L;
		User user = createUser(userId);
		when(profileRepository.getProfileByUserId(userId)).thenReturn(null);
		when(userQueryUseCase.findById(userId)).thenReturn(user);
		when(projectQueryUseCase.queryByUserId(userId)).thenReturn(new ArrayList<>());

		// when
		UserProfileResponse response = service.getProfileByUserId(userId);

		// then
		assertNotNull(response);
		assertEquals(userId, response.userId());
		assertEquals(user.getEmail(), response.email());
		assertEquals(user.getName(), response.name());
		assertNull(response.profileId());
		assertTrue(response.roles().isEmpty());
		assertTrue(response.schools().isEmpty());
		assertTrue(response.skills().isEmpty());
		assertTrue(response.portfolios().isEmpty());
		assertTrue(response.projects().isEmpty());

		verify(profileRepository).getProfileByUserId(userId);
		verify(userQueryUseCase).findById(userId);
		verify(projectQueryUseCase).queryByUserId(userId);
	}

	@Test
	void 프로필이_있을_때_전체_정보를_반환한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		User user = createUser(userId);
		Profile profile = createProfile(profileId, userId);

		// Profile 관련 데이터
		List<ProfileRole> profileRoles = createProfileRoles(profileId);
		List<ProfileSchool> profileSchools = createProfileSchools(profileId);
		List<ProfileSkill> profileSkills = createProfileSkills(profileId);
		List<ProjectPortfolio> projectPortfolios = createProjectPortfolios(profileId);

		// 마스터 데이터
		List<Role> roles = createRoles();
		List<School> schools = createSchools();
		List<Skill> skills = createSkills();
		List<Portfolio> portfolios = createPortfolios();
		List<Project> projects = createProjects();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(userQueryUseCase.findById(userId)).thenReturn(user);
		when(profileRepository.getProfileRoles(profileId)).thenReturn(profileRoles);
		when(profileRepository.getProfileSchools(profileId)).thenReturn(profileSchools);
		when(profileRepository.getProfileSkills(profileId)).thenReturn(profileSkills);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(projectPortfolios);
		when(roleRepository.findByIdIn(anyList())).thenReturn(roles);
		when(schoolRepository.findByIdIn(anyList())).thenReturn(schools);
		when(skillRepository.findByIdIn(anyList())).thenReturn(skills);
		when(portfolioRepository.findByIdIn(anyList())).thenReturn(portfolios);
		when(projectQueryUseCase.queryByUserId(userId)).thenReturn(projects);

		// when
		UserProfileResponse response = service.getProfileByUserId(userId);

		// then
		assertNotNull(response);
		assertEquals(userId, response.userId());
		assertEquals(profileId, response.profileId());
		assertEquals(2, response.roles().size());
		assertEquals(1, response.schools().size());
		assertEquals(3, response.skills().size());
		assertEquals(2, response.portfolios().size());
		assertEquals(2, response.projects().size());

		verify(profileRepository).getProfileByUserId(userId);
		verify(userQueryUseCase).findById(userId);
		verify(profileRepository).getProfileRoles(profileId);
		verify(profileRepository).getProfileSchools(profileId);
		verify(profileRepository).getProfileSkills(profileId);
		verify(profileRepository).getProjectPortfolios(profileId);
		verify(projectQueryUseCase).queryByUserId(userId);
	}

	@Test
	void 프로필은_있지만_연관_데이터가_없을_때_빈_리스트를_반환한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		User user = createUser(userId);
		Profile profile = createProfile(profileId, userId);

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(userQueryUseCase.findById(userId)).thenReturn(user);
		when(profileRepository.getProfileRoles(profileId)).thenReturn(new ArrayList<>());
		when(profileRepository.getProfileSchools(profileId)).thenReturn(new ArrayList<>());
		when(profileRepository.getProfileSkills(profileId)).thenReturn(new ArrayList<>());
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(new ArrayList<>());
		when(projectQueryUseCase.queryByUserId(userId)).thenReturn(new ArrayList<>());

		// when
		UserProfileResponse response = service.getProfileByUserId(userId);

		// then
		assertNotNull(response);
		assertEquals(profileId, response.profileId());
		assertTrue(response.roles().isEmpty());
		assertTrue(response.schools().isEmpty());
		assertTrue(response.skills().isEmpty());
		assertTrue(response.portfolios().isEmpty());
		assertTrue(response.projects().isEmpty());
	}

	@Test
	void 프로필이_없을_때_프로젝트_정보도_포함하여_반환한다() {
		// given
		Long userId = 1L;
		User user = createUser(userId);
		List<Project> projects = createProjects();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(null);
		when(userQueryUseCase.findById(userId)).thenReturn(user);
		when(projectQueryUseCase.queryByUserId(userId)).thenReturn(projects);

		// when
		UserProfileResponse response = service.getProfileByUserId(userId);

		// then
		assertNotNull(response);
		assertEquals(userId, response.userId());
		assertNull(response.profileId());
		assertEquals(2, response.projects().size());
		assertEquals("협업 플랫폼 개발", response.projects().get(0).title());
		assertEquals(MeetingType.HYBRID, response.projects().get(0).meetingType());
		assertEquals(ProjectStatus.RECRUITING, response.projects().get(0).status());

		verify(projectQueryUseCase).queryByUserId(userId);
	}

	@Test
	void 프로젝트가_없을_때_빈_리스트를_반환한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		User user = createUser(userId);
		Profile profile = createProfile(profileId, userId);

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(userQueryUseCase.findById(userId)).thenReturn(user);
		when(profileRepository.getProfileRoles(profileId)).thenReturn(new ArrayList<>());
		when(profileRepository.getProfileSchools(profileId)).thenReturn(new ArrayList<>());
		when(profileRepository.getProfileSkills(profileId)).thenReturn(new ArrayList<>());
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(new ArrayList<>());
		when(projectQueryUseCase.queryByUserId(userId)).thenReturn(new ArrayList<>());

		// when
		UserProfileResponse response = service.getProfileByUserId(userId);

		// then
		assertNotNull(response);
		assertTrue(response.projects().isEmpty());
		verify(projectQueryUseCase).queryByUserId(userId);
	}

	private User createUser(Long userId) {
		User user = User.builder()
			.id(userId)
			.email("test@test.com")
			.name("홍길동")
			.nickname("길동이")
			.password("password123!")
			.age(25)
			.tel("010-1234-5678")
			.type(UserType.LOCAL)
			.isActive(true)
			.build();
		return user;
	}

	private Profile createProfile(Long profileId, Long userId) {
		return Profile.builder()
			.id(profileId)
			.userId(userId)
			.build();
	}

	private List<ProfileRole> createProfileRoles(Long profileId) {
		return List.of(
			ProfileRole.builder().id(1L).profileId(profileId).roleId(1L).build(),
			ProfileRole.builder().id(2L).profileId(profileId).roleId(2L).build()
		);
	}

	private List<ProfileSchool> createProfileSchools(Long profileId) {
		return List.of(
			ProfileSchool.builder()
				.id(1L)
				.profileId(profileId)
				.schoolId(1L)
				.state(SchoolStateType.ENROLLED)
				.major("컴퓨터공학과")
				.startDate(LocalDate.of(2020, 3, 1))
				.endDate(LocalDate.of(2024, 2, 29))
				.build()
		);
	}

	private List<ProfileSkill> createProfileSkills(Long profileId) {
		return List.of(
			ProfileSkill.builder().id(1L).profileId(profileId).skillId(1L).build(),
			ProfileSkill.builder().id(2L).profileId(profileId).skillId(2L).build(),
			ProfileSkill.builder().id(3L).profileId(profileId).skillId(3L).build()
		);
	}

	private List<ProjectPortfolio> createProjectPortfolios(Long profileId) {
		return List.of(
			ProjectPortfolio.builder().id(1L).profileId(profileId).portfolioId(1L).build(),
			ProjectPortfolio.builder().id(2L).profileId(profileId).portfolioId(2L).build()
		);
	}

	private List<Role> createRoles() {
		return List.of(
			Role.builder().id(1L).name("백엔드 개발자").build(),
			Role.builder().id(2L).name("프론트엔드 개발자").build()
		);
	}

	private List<School> createSchools() {
		return List.of(
			School.builder()
				.id(1L)
				.name("서울대학교")
				.address("서울특별시 관악구 관악로 1")
				.build()
		);
	}

	private List<Skill> createSkills() {
		return List.of(
			Skill.builder().id(1L).name("Java").build(),
			Skill.builder().id(2L).name("Spring Boot").build(),
			Skill.builder().id(3L).name("React").build()
		);
	}

	private List<Portfolio> createPortfolios() {
		return List.of(
			Portfolio.builder()
				.id(1L)
				.type(PortfolioType.INTERN)
				.startDate(LocalDate.of(2023, 6, 1))
				.endDate(LocalDate.of(2023, 8, 31))
				.content("네이버 백엔드 인턴십")
				.build(),
			Portfolio.builder()
				.id(2L)
				.type(PortfolioType.PROJECT)
				.startDate(LocalDate.of(2023, 9, 1))
				.endDate(LocalDate.of(2024, 1, 31))
				.content("사이드 프로젝트")
				.build()
		);
	}

	private List<Project> createProjects() {
		return List.of(
			Project.builder()
				.id(1L)
				.title("협업 플랫폼 개발")
				.description("사이드 프로젝트 협업을 위한 플랫폼 개발 프로젝트입니다.")
				.startDt(LocalDate.of(2024, 1, 1))
				.endDt(LocalDate.of(2024, 6, 30))
				.meetingType(MeetingType.HYBRID)
				.status(ProjectStatus.RECRUITING)
				.build(),
			Project.builder()
				.id(2L)
				.title("웹 서비스 구축")
				.description("React와 Spring Boot를 활용한 풀스택 웹 서비스 개발")
				.startDt(LocalDate.of(2024, 3, 1))
				.endDt(LocalDate.of(2024, 8, 31))
				.meetingType(MeetingType.ONLINE)
				.status(ProjectStatus.PREPARING)
				.build()
		);
	}
}

