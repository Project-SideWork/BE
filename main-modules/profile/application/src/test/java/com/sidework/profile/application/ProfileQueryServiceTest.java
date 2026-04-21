package com.sidework.profile.application;

import com.sidework.profile.application.adapter.UserProfileResponse;
import com.sidework.profile.application.adapter.UserProfileListResponse;
import com.sidework.profile.application.adapter.UserProjectDto;
import com.sidework.profile.application.port.out.PortfolioOutPort;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.application.port.out.RoleOutPort;
import com.sidework.profile.application.port.in.ProfileLikeQueryUseCase;
import com.sidework.school.application.port.in.SchoolQueryUseCase;
import com.sidework.profile.application.service.ProfileQueryService;
import com.sidework.profile.domain.Portfolio;
import com.sidework.profile.domain.PortfolioType;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.domain.ProjectPortfolio;
import com.sidework.profile.domain.Role;
import com.sidework.school.domain.School;
import com.sidework.profile.domain.SchoolStateType;
import com.sidework.project.application.dto.ProjectUserReviewStatSummary;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.skill.application.service.ProjectRequiredSkillQueryService;
import com.sidework.skill.application.port.out.SkillOutPort;
import com.sidework.skill.domain.Skill;
import com.sidework.user.application.port.in.UserQueryUseCase;
import com.sidework.user.domain.User;
import com.sidework.user.domain.UserType;
import com.sidework.common.response.PageResponse;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceTest {

	@Mock
	private ProfileOutPort profileRepository;

	@Mock
	private SchoolQueryUseCase schoolQueryUseCase;

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

	@Mock
	private ProjectRequiredSkillQueryService requiredSkillUseCase;

	@Mock
	private ProfileLikeQueryUseCase profileLikeQueryUseCase;

	@InjectMocks
	private ProfileQueryService service;

	@BeforeEach
	void setUp() {
		lenient().when(projectQueryUseCase.queryStatSummaryByUserId(anyLong()))
			.thenReturn(new ProjectUserReviewStatSummary(8.0, 2L));
		lenient().when(projectQueryUseCase.queryReviewSummaryByProjectIds(anyLong(), anyList()))
			.thenReturn(List.of());
	}

	@Test
	void 프로필_목록_조회시_liked를_반환한다() {
		// given
		Long viewerUserId = 100L;
		List<Long> skillIds = List.of(11L, 12L);
		var pageable = PageRequest.of(0, 2);

		Profile p1 = Profile.builder().id(10L).userId(1L).selfIntroduction("소개1").build();
		Profile p2 = Profile.builder().id(20L).userId(2L).selfIntroduction("소개2").build();

		when(profileRepository.searchProfilesBySkillName(skillIds, pageable))
			.thenReturn(new PageImpl<>(List.of(p1, p2), pageable, 5));

		when(userQueryUseCase.findNamesByUserIds(anyList()))
			.thenReturn(Map.of(1L, "김철수", 2L, "이영희"));

		List<ProfileSkill> profileSkills = List.of(
			ProfileSkill.builder().id(1L).profileId(10L).skillId(11L).proficiency(null).build(),
			ProfileSkill.builder().id(2L).profileId(20L).skillId(12L).proficiency(null).build()
		);
		when(profileRepository.getProfileSkillsByProfileIds(anyList())).thenReturn(profileSkills);

		when(skillRepository.findByIdIn(anyList()))
			.thenReturn(List.of(
				Skill.builder().id(11L).name("Java").build(),
				Skill.builder().id(12L).name("Spring").build()
			));

		when(profileLikeQueryUseCase.isLikedByProfileIds(eq(viewerUserId), anyList()))
			.thenReturn(Map.of(10L, true, 20L, false));

		when(projectQueryUseCase.queryAverageReviewScoresByUserIds(anyList()))
			.thenReturn(Map.of(1L, 4.5, 2L, 3.0));

		// when
		PageResponse<List<UserProfileListResponse>> res = service.getUserProfileList(viewerUserId, skillIds, pageable);

		// then
		assertNotNull(res);
		assertEquals(1, res.page()); // PageResponse.of는 page+1
		assertEquals(2, res.size());
		assertEquals(5, res.totalElements());
		assertEquals(3, res.totalPages());

		assertNotNull(res.content());
		assertEquals(2, res.content().size());
		assertTrue(res.content().get(0).liked());
		assertFalse(res.content().get(1).liked());
		assertEquals(4.5, res.content().get(0).score());
		assertEquals(3.0, res.content().get(1).score());
	}

	@Test
	void 프로필_목록_조회시_빈_페이지면_빈_컨텐츠로_응답한다() {
		// given
		Long viewerUserId = 100L;
		List<Long> skillIds = List.of(11L);
		var pageable = PageRequest.of(0, 20);

		when(profileRepository.searchProfilesBySkillName(skillIds, pageable))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		// when
		PageResponse<List<UserProfileListResponse>> res = service.getUserProfileList(viewerUserId, skillIds, pageable);

		// then
		assertNotNull(res);
		assertEquals(1, res.page());
		assertEquals(20, res.size());
		assertEquals(0, res.totalElements());
		assertEquals(0, res.totalPages());
		assertNotNull(res.content());
		assertTrue(res.content().isEmpty());
	}

	@Test
	void 좋아요_프로필_목록_조회시_페이지네이션_메타와_liked를_반환한다() {
		// given
		Long viewerUserId = 100L;
		List<Long> skillIds = List.of(11L, 12L);
		var pageable = PageRequest.of(0, 2);

		Profile p1 = Profile.builder().id(10L).userId(1L).selfIntroduction("소개1").build();
		Profile p2 = Profile.builder().id(20L).userId(2L).selfIntroduction("소개2").build();

		when(profileRepository.searchLikedProfilesBySkillName(viewerUserId, skillIds, pageable))
			.thenReturn(new PageImpl<>(List.of(p1, p2), pageable, 5));

		when(userQueryUseCase.findNamesByUserIds(anyList()))
			.thenReturn(Map.of(1L, "김철수", 2L, "이영희"));

		List<ProfileSkill> profileSkills = List.of(
			ProfileSkill.builder().id(1L).profileId(10L).skillId(11L).proficiency(null).build(),
			ProfileSkill.builder().id(2L).profileId(20L).skillId(12L).proficiency(null).build()
		);
		when(profileRepository.getProfileSkillsByProfileIds(anyList())).thenReturn(profileSkills);

		when(skillRepository.findByIdIn(anyList()))
			.thenReturn(List.of(
				Skill.builder().id(11L).name("Java").build(),
				Skill.builder().id(12L).name("Spring").build()
			));

		when(projectQueryUseCase.queryAverageReviewScoresByUserIds(anyList()))
			.thenReturn(Map.of(1L, 5.0, 2L, 4.0));

		// when
		PageResponse<List<UserProfileListResponse>> res = service.getLikedUserProfileList(viewerUserId, skillIds, pageable);

		// then
		assertNotNull(res);
		assertEquals(1, res.page()); // PageResponse.of는 page+1
		assertEquals(2, res.size());
		assertEquals(5, res.totalElements());
		assertEquals(3, res.totalPages());
		assertNotNull(res.content());
		assertEquals(2, res.content().size());
		assertTrue(res.content().get(0).liked());
		assertTrue(res.content().get(1).liked());
		assertEquals(5.0, res.content().get(0).score());
		assertEquals(4.0, res.content().get(1).score());
	}

	@Test
	void 좋아요_프로필_목록_조회시_빈_페이지면_빈_컨텐츠로_응답한다() {
		// given
		Long viewerUserId = 100L;
		List<Long> skillIds = List.of(11L);
		var pageable = PageRequest.of(0, 20);

		when(profileRepository.searchLikedProfilesBySkillName(viewerUserId, skillIds, pageable))
			.thenReturn(new PageImpl<>(List.of(), pageable, 0));

		// when
		PageResponse<List<UserProfileListResponse>> res = service.getLikedUserProfileList(viewerUserId, skillIds, pageable);

		// then
		assertNotNull(res);
		assertEquals(1, res.page());
		assertEquals(20, res.size());
		assertEquals(0, res.totalElements());
		assertEquals(0, res.totalPages());
		assertNotNull(res.content());
		assertTrue(res.content().isEmpty());
	}

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
		when(schoolQueryUseCase.findByIdIn(anyList())).thenReturn(schools);
		when(skillRepository.findByIdIn(anyList())).thenReturn(skills);
		when(portfolioRepository.findByIdIn(anyList())).thenReturn(portfolios);
		when(projectQueryUseCase.queryByUserId(userId)).thenReturn(projects);
		when(projectQueryUseCase.queryUserRolesByProjects(userId, List.of(1L, 2L)))
			.thenReturn(Map.of(1L, List.of(ProjectRole.BACKEND), 2L, List.of(ProjectRole.FRONTEND)));
		when(requiredSkillUseCase.queryNamesByProjectIds(List.of(1L, 2L)))
			.thenReturn(Map.of(1L, List.of("Java", "Spring"), 2L, List.of("React")));

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

		verify(profileRepository).getProfileByUserId(userId);
		verify(userQueryUseCase).findById(userId);
		verify(profileRepository).getProfileRoles(profileId);
		verify(profileRepository).getProfileSchools(profileId);
		verify(profileRepository).getProfileSkills(profileId);
		verify(profileRepository).getProjectPortfolios(profileId);
		verify(projectQueryUseCase).queryByUserId(userId);
		verify(requiredSkillUseCase).queryNamesByProjectIds(anyList());
		verify(projectQueryUseCase).queryUserRolesByProjects(anyLong(), anyList());
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
		when(projectQueryUseCase.queryUserRolesByProjects(userId, List.of(1L, 2L)))
			.thenReturn(Map.of(1L, List.of(), 2L, List.of()));
		when(requiredSkillUseCase.queryNamesByProjectIds(List.of(1L, 2L)))
			.thenReturn(Map.of(1L, List.of(), 2L, List.of()));

		// when
		UserProfileResponse response = service.getProfileByUserId(userId);

		// then
		assertNotNull(response);
		assertEquals(userId, response.userId());
		assertNull(response.profileId());

		verify(projectQueryUseCase).queryByUserId(userId);
		verify(requiredSkillUseCase).queryNamesByProjectIds(anyList());
		verify(projectQueryUseCase).queryUserRolesByProjects(anyLong(), anyList());
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
		verify(projectQueryUseCase).queryByUserId(userId);
	}

    @Test
    void 내_프로젝트_목록_조회시_페이지_메타와_프로젝트_정보를_반환한다() {
        // given
        Long viewerUserId = 1L;
        var pageable = PageRequest.of(0, 5);

        List<Project> projects = List.of(
                Project.builder()
                        .id(23L)
                        .title("테스트 프로젝트 A")
                        .description("설명 A")
                        .startDt(LocalDate.of(2026, 4, 17))
                        .endDt(LocalDate.of(2026, 6, 17))
                        .meetingType(MeetingType.HYBRID)
                        .status(ProjectStatus.PREPARING)
                        .build(),
                Project.builder()
                        .id(22L)
                        .title("테스트 프로젝트 B")
                        .description("설명 B")
                        .startDt(LocalDate.of(2026, 3, 1))
                        .endDt(LocalDate.of(2026, 5, 1))
                        .meetingType(MeetingType.OFFLINE)
                        .status(ProjectStatus.RECRUITING)
                        .build()
        );

        when(projectQueryUseCase.pageByUserId(viewerUserId, pageable)).thenReturn(projects);
        when(projectQueryUseCase.queryProjectCount(viewerUserId)).thenReturn(16L);
        when(requiredSkillUseCase.queryNamesByProjectIds(List.of(23L, 22L)))
                .thenReturn(Map.of(
                        23L, List.of("Java", "Spring"),
                        22L, List.of("React")
                ));
        when(projectQueryUseCase.queryUserRolesByProjects(viewerUserId, List.of(23L, 22L)))
                .thenReturn(Map.of(
                        23L, List.of(ProjectRole.OWNER, ProjectRole.BACKEND),
                        22L, List.of(ProjectRole.FRONTEND)
                ));

        // when
        PageResponse<List<UserProjectDto>> response = service.getUserProjectList(viewerUserId, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.page());
        assertEquals(5, response.size());
        assertEquals(16L, response.totalElements());
        assertEquals(4, response.totalPages());

        assertNotNull(response.content());
        assertEquals(2, response.content().size());

        UserProjectDto first = response.content().get(0);
        assertEquals(23L, first.projectId());
        assertEquals("테스트 프로젝트 A", first.title());
        assertEquals("설명 A", first.description());
        assertEquals(MeetingType.HYBRID, first.meetingType());
        assertEquals(ProjectStatus.PREPARING, first.status());
        assertEquals(List.of("Java", "Spring"), first.projectStacks());
        assertEquals(List.of(ProjectRole.OWNER, ProjectRole.BACKEND), first.role());

        UserProjectDto second = response.content().get(1);
        assertEquals(22L, second.projectId());
        assertEquals(List.of("React"), second.projectStacks());
        assertEquals(List.of(ProjectRole.FRONTEND), second.role());

        verify(projectQueryUseCase).pageByUserId(viewerUserId, pageable);
        verify(projectQueryUseCase).queryProjectCount(viewerUserId);
        verify(requiredSkillUseCase).queryNamesByProjectIds(List.of(23L, 22L));
        verify(projectQueryUseCase).queryUserRolesByProjects(viewerUserId, List.of(23L, 22L));
    }

    @Test
    void 내_프로젝트_목록_조회시_프로젝트가_없으면_빈_리스트를_반환한다() {
        // given
        Long viewerUserId = 1L;
        var pageable = PageRequest.of(0, 5);

        when(projectQueryUseCase.pageByUserId(viewerUserId, pageable)).thenReturn(List.of());
        when(projectQueryUseCase.queryProjectCount(viewerUserId)).thenReturn(0L);

        // when
        PageResponse<List<UserProjectDto>> response = service.getUserProjectList(viewerUserId, pageable);

        // then
        assertNotNull(response);
        assertEquals(1, response.page());
        assertEquals(5, response.size());
        assertEquals(0L, response.totalElements());
        assertEquals(0, response.totalPages());
        assertNotNull(response.content());
        assertTrue(response.content().isEmpty());

        verify(projectQueryUseCase).pageByUserId(viewerUserId, pageable);
        verify(projectQueryUseCase).queryProjectCount(viewerUserId);
        verify(requiredSkillUseCase, never()).queryNamesByProjectIds(anyList());
        verify(projectQueryUseCase, never()).queryUserRolesByProjects(anyLong(), anyList());
    }

    @Test
    void 내_프로젝트_목록_조회시_현재_페이지_프로젝트가_없어도_전체_카운트는_반환한다() {
        // given
        Long viewerUserId = 1L;
        var pageable = PageRequest.of(2, 5); // 3페이지 요청

        when(projectQueryUseCase.pageByUserId(viewerUserId, pageable)).thenReturn(List.of());
        when(projectQueryUseCase.queryProjectCount(viewerUserId)).thenReturn(11L);

        // when
        PageResponse<List<UserProjectDto>> response = service.getUserProjectList(viewerUserId, pageable);

        // then
        assertNotNull(response);
        assertEquals(3, response.page());
        assertEquals(5, response.size());
        assertEquals(11L, response.totalElements());
        assertEquals(3, response.totalPages());
        assertNotNull(response.content());
        assertTrue(response.content().isEmpty());

        verify(projectQueryUseCase).pageByUserId(viewerUserId, pageable);
        verify(projectQueryUseCase).queryProjectCount(viewerUserId);
    }

    @Test
    void 내_프로젝트_목록_조회시_전체_페이지를_올림으로_계산한다() {
        // given
        Long viewerUserId = 1L;
        var pageable = PageRequest.of(0, 5);

        List<Project> projects = List.of(
                Project.builder()
                        .id(1L)
                        .title("프로젝트")
                        .description("설명")
                        .startDt(LocalDate.of(2026, 1, 1))
                        .endDt(LocalDate.of(2026, 2, 1))
                        .meetingType(MeetingType.ONLINE)
                        .status(ProjectStatus.PREPARING)
                        .build()
        );

        when(projectQueryUseCase.pageByUserId(viewerUserId, pageable)).thenReturn(projects);
        when(projectQueryUseCase.queryProjectCount(viewerUserId)).thenReturn(6L);
        when(requiredSkillUseCase.queryNamesByProjectIds(List.of(1L)))
                .thenReturn(Map.of(1L, List.of("Java")));
        when(projectQueryUseCase.queryUserRolesByProjects(viewerUserId, List.of(1L)))
                .thenReturn(Map.of(1L, List.of(ProjectRole.BACKEND)));

        // when
        PageResponse<List<UserProjectDto>> response = service.getUserProjectList(viewerUserId, pageable);

        // then
        assertEquals(2, response.totalPages()); // 6 / 5 = 1.2 -> 올림 2
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

