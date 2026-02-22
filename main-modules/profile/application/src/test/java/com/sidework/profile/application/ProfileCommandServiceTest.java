package com.sidework.profile.application;

import com.sidework.profile.application.exception.ProfileNotFoundException;
import com.sidework.profile.application.port.in.ProfileUpdateCommand;
import com.sidework.profile.application.port.out.PortfolioOutPort;
import com.sidework.profile.application.port.out.ProfileOutPort;
import com.sidework.profile.application.service.ProfileCommandService;
import com.sidework.profile.domain.Portfolio;
import com.sidework.profile.domain.PortfolioType;
import com.sidework.profile.domain.Profile;
import com.sidework.profile.domain.ProfileRole;
import com.sidework.profile.domain.ProfileSchool;
import com.sidework.profile.domain.ProfileSkill;
import com.sidework.profile.domain.ProjectPortfolio;
import com.sidework.profile.domain.SchoolStateType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileCommandServiceTest {

	@Mock
	private ProfileOutPort profileRepository;

	@Mock
	private PortfolioOutPort portfolioRepository;

	@InjectMocks
	private ProfileCommandService service;

	@Captor
	private ArgumentCaptor<List<ProfileSchool>> profileSchoolCaptor;

	@Captor
	private ArgumentCaptor<List<ProfileSkill>> profileSkillCaptor;

	@Captor
	private ArgumentCaptor<List<ProfileRole>> profileRoleCaptor;

	@Captor
	private ArgumentCaptor<List<ProjectPortfolio>> projectPortfolioCaptor;

	@Captor
	private ArgumentCaptor<List<Portfolio>> portfolioCaptor;

	@Captor
	private ArgumentCaptor<List<Long>> portfolioIdsCaptor;

	@Test
	void 프로필이_없을_때_ProfileNotFoundException을_던진다() {
		// given
		Long userId = 1L;
		ProfileUpdateCommand command = createEmptyCommand();
		when(profileRepository.getProfileByUserId(userId)).thenReturn(null);

		// when & then
		assertThrows(
			ProfileNotFoundException.class,
			() -> service.update(userId, command)
		);

		verify(profileRepository).getProfileByUserId(userId);
		verify(profileRepository, never()).saveProfileSchools(anyList());
		verify(profileRepository, never()).saveProfileSkills(anyList());
		verify(profileRepository, never()).saveProfileRoles(anyList());
	}

	@Test
	void Schools_업데이트에_성공한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithSchools();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);

		// when
		service.update(userId, command);

		// then
		verify(profileRepository).deleteAllProfileSchoolsByProfileId(profileId);
		verify(profileRepository).saveProfileSchools(profileSchoolCaptor.capture());

		List<ProfileSchool> savedSchools = profileSchoolCaptor.getValue();
		assertEquals(1, savedSchools.size());
		assertEquals(profileId, savedSchools.get(0).getProfileId());
		assertEquals(1L, savedSchools.get(0).getSchoolId());
		assertEquals(SchoolStateType.ENROLLED, savedSchools.get(0).getState());
		assertEquals("컴퓨터공학과", savedSchools.get(0).getMajor());
	}

	@Test
	void Skills_업데이트에_성공한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithSkills();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);

		// when
		service.update(userId, command);

		// then
		verify(profileRepository).deleteAllProfileSkillsByProfileId(profileId);
		verify(profileRepository).saveProfileSkills(profileSkillCaptor.capture());

		List<ProfileSkill> savedSkills = profileSkillCaptor.getValue();
		assertEquals(2, savedSkills.size());
		assertEquals(profileId, savedSkills.get(0).getProfileId());
		assertEquals(1L, savedSkills.get(0).getSkillId());
		assertEquals(2L, savedSkills.get(1).getSkillId());
	}

	@Test
	void Roles_업데이트에_성공한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithRoles();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);

		// when
		service.update(userId, command);

		// then
		verify(profileRepository).deleteAllProfileRolesByProfileId(profileId);
		verify(profileRepository).saveProfileRoles(profileRoleCaptor.capture());

		List<ProfileRole> savedRoles = profileRoleCaptor.getValue();
		assertEquals(2, savedRoles.size());
		assertEquals(profileId, savedRoles.get(0).getProfileId());
		assertEquals(1L, savedRoles.get(0).getRoleId());
		assertEquals(3L, savedRoles.get(1).getRoleId());
	}

	@Test
	void Portfolios가_비어있을_때_기존_포트폴리오만_삭제한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithEmptyPortfolios();

		List<ProjectPortfolio> existingPortfolios = List.of(
			ProjectPortfolio.builder().id(1L).profileId(profileId).portfolioId(10L).build(),
			ProjectPortfolio.builder().id(2L).profileId(profileId).portfolioId(20L).build()
		);

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(existingPortfolios);
		when(profileRepository.findPortfolioIdsReferencedByOtherProfiles(profileId, List.of(10L, 20L))).thenReturn(List.of());

		// when
		service.update(userId, command);

		// then
		verify(profileRepository).getProjectPortfolios(profileId);
		verify(profileRepository).deleteAllProjectPortfoliosByProfileId(profileId);
		verify(profileRepository).findPortfolioIdsReferencedByOtherProfiles(profileId, List.of(10L, 20L));
		verify(portfolioRepository).deletePortfolios(portfolioIdsCaptor.capture());
		verify(portfolioRepository, never()).savePortfolios(anyList());
		verify(profileRepository, never()).saveProjectPortfolios(anyList());

		List<Long> deletedIds = portfolioIdsCaptor.getValue();
		assertEquals(2, deletedIds.size());
		assertTrue(deletedIds.contains(10L));
		assertTrue(deletedIds.contains(20L));
	}

	@Test
	void Portfolios_업데이트_기존_포트폴리오_UPDATE에_성공한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Long portfolioId = 100L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithExistingPortfolio(portfolioId);

		Portfolio existingPortfolio = Portfolio.builder()
			.id(portfolioId)
			.type(PortfolioType.INTERN)
			.startDate(LocalDate.of(2023, 1, 1))
			.endDate(LocalDate.of(2023, 3, 1))
			.content("기존 내용")
			.build();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(new ArrayList<>());
		when(portfolioRepository.findByIdIn(List.of(portfolioId))).thenReturn(List.of(existingPortfolio));
		when(portfolioRepository.savePortfolios(anyList())).thenReturn(List.of(portfolioId));

		// when
		service.update(userId, command);

		// then
		verify(portfolioRepository).findByIdIn(List.of(portfolioId));
		verify(portfolioRepository).savePortfolios(portfolioCaptor.capture());
		verify(profileRepository).saveProjectPortfolios(anyList());

		List<Portfolio> savedPortfolios = portfolioCaptor.getValue();
		assertEquals(1, savedPortfolios.size());
		assertEquals(portfolioId, savedPortfolios.get(0).getId());
		assertEquals(PortfolioType.PROJECT, savedPortfolios.get(0).getType());
		assertEquals("수정된 내용", savedPortfolios.get(0).getContent());
	}

	@Test
	void Portfolios_업데이트_새_포트폴리오_INSERT에_성공한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithNewPortfolio();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(new ArrayList<>());
		when(portfolioRepository.savePortfolios(anyList())).thenReturn(List.of(200L));

		// when
		service.update(userId, command);

		// then
		verify(portfolioRepository).savePortfolios(portfolioCaptor.capture());
		verify(profileRepository).saveProjectPortfolios(projectPortfolioCaptor.capture());

		List<Portfolio> savedPortfolios = portfolioCaptor.getValue();
		assertEquals(1, savedPortfolios.size());
		assertNull(savedPortfolios.get(0).getId());
		assertEquals(PortfolioType.INTERN, savedPortfolios.get(0).getType());

		List<ProjectPortfolio> savedProjectPortfolios = projectPortfolioCaptor.getValue();
		assertEquals(1, savedProjectPortfolios.size());
		assertEquals(profileId, savedProjectPortfolios.get(0).getProfileId());
		assertEquals(200L, savedProjectPortfolios.get(0).getPortfolioId());
	}

	@Test
	void Portfolios_업데이트_기존_포트폴리오_삭제_다른_프로필이_참조하지_않을_때만_삭제한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Long existingPortfolioId = 10L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithNewPortfolio();

		List<ProjectPortfolio> existingPortfolios = List.of(
			ProjectPortfolio.builder().id(1L).profileId(profileId).portfolioId(existingPortfolioId).build()
		);

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(existingPortfolios);
		when(profileRepository.findPortfolioIdsReferencedByOtherProfiles(profileId, List.of(existingPortfolioId))).thenReturn(List.of());
		when(portfolioRepository.savePortfolios(anyList())).thenReturn(List.of(200L));

		// when
		service.update(userId, command);

		// then
		verify(profileRepository).findPortfolioIdsReferencedByOtherProfiles(profileId, List.of(existingPortfolioId));
		verify(portfolioRepository).deletePortfolios(List.of(existingPortfolioId));
	}

	@Test
	void Portfolios_업데이트_다른_프로필이_참조하는_포트폴리오는_삭제하지_않는다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Long existingPortfolioId = 10L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithNewPortfolio();

		List<ProjectPortfolio> existingPortfolios = List.of(
			ProjectPortfolio.builder().id(1L).profileId(profileId).portfolioId(existingPortfolioId).build()
		);

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(existingPortfolios);
		when(profileRepository.findPortfolioIdsReferencedByOtherProfiles(profileId, List.of(existingPortfolioId))).thenReturn(List.of(existingPortfolioId));
		when(portfolioRepository.savePortfolios(anyList())).thenReturn(List.of(200L));

		// when
		service.update(userId, command);

		// then
		verify(profileRepository).findPortfolioIdsReferencedByOtherProfiles(profileId, List.of(existingPortfolioId));
		verify(portfolioRepository, never()).deletePortfolios(anyList());
	}

	@Test
	void Portfolios_업데이트_요청된_포트폴리오_ID가_DB에_없으면_새_포트폴리오로_생성한다() {
		// given: 요청에는 portfolioId가 있지만 findByIdIn이 빈 리스트 반환 (DB에 해당 ID 없음)
		Long userId = 1L;
		Long profileId = 1L;
		Long requestedPortfolioId = 999L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithExistingPortfolio(requestedPortfolioId);

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(new ArrayList<>());
		when(portfolioRepository.findByIdIn(List.of(requestedPortfolioId))).thenReturn(List.of());
		when(portfolioRepository.savePortfolios(anyList())).thenReturn(List.of(200L));

		// when
		service.update(userId, command);

		// then: existingPortfolioMap에 없으므로 새 포트폴리오(Portfolio.create)로 저장된다
		verify(portfolioRepository).findByIdIn(List.of(requestedPortfolioId));
		verify(portfolioRepository).savePortfolios(portfolioCaptor.capture());
		verify(profileRepository).saveProjectPortfolios(projectPortfolioCaptor.capture());

		List<Portfolio> savedPortfolios = portfolioCaptor.getValue();
		assertEquals(1, savedPortfolios.size());
		assertNull(savedPortfolios.get(0).getId());
		assertEquals(PortfolioType.PROJECT, savedPortfolios.get(0).getType());
		assertEquals(LocalDate.of(2023, 6, 1), savedPortfolios.get(0).getStartDate());
		assertEquals(LocalDate.of(2023, 8, 31), savedPortfolios.get(0).getEndDate());
		assertEquals("수정된 내용", savedPortfolios.get(0).getContent());

		List<ProjectPortfolio> savedProjectPortfolios = projectPortfolioCaptor.getValue();
		assertEquals(1, savedProjectPortfolios.size());
		assertEquals(profileId, savedProjectPortfolios.get(0).getProfileId());
		assertEquals(200L, savedProjectPortfolios.get(0).getPortfolioId());
	}

	@Test
	void Portfolios_업데이트_기존_포트폴리오_UPDATE와_새_포트폴리오_INSERT를_동시에_처리한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Long existingPortfolioId = 100L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createCommandWithMixedPortfolios(existingPortfolioId);

		Portfolio existingPortfolio = Portfolio.builder()
			.id(existingPortfolioId)
			.type(PortfolioType.INTERN)
			.startDate(LocalDate.of(2023, 1, 1))
			.endDate(LocalDate.of(2023, 3, 1))
			.content("기존 내용")
			.build();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(new ArrayList<>());
		when(portfolioRepository.findByIdIn(List.of(existingPortfolioId))).thenReturn(List.of(existingPortfolio));
		when(portfolioRepository.savePortfolios(anyList())).thenReturn(List.of(existingPortfolioId, 200L));

		// when
		service.update(userId, command);

		// then
		verify(portfolioRepository).savePortfolios(portfolioCaptor.capture());
		verify(profileRepository).saveProjectPortfolios(projectPortfolioCaptor.capture());

		List<Portfolio> savedPortfolios = portfolioCaptor.getValue();
		assertEquals(2, savedPortfolios.size());
		assertEquals(existingPortfolioId, savedPortfolios.get(0).getId());
		assertNull(savedPortfolios.get(1).getId());

		List<ProjectPortfolio> savedProjectPortfolios = projectPortfolioCaptor.getValue();
		assertEquals(2, savedProjectPortfolios.size());
	}

	@Test
	void 전체_업데이트에_성공한다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = createFullCommand();

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);
		when(profileRepository.getProjectPortfolios(profileId)).thenReturn(new ArrayList<>());
		when(portfolioRepository.savePortfolios(anyList())).thenReturn(List.of(1L));

		// when
		service.update(userId, command);

		// then
		verify(profileRepository).deleteAllProfileSchoolsByProfileId(profileId);
		verify(profileRepository).saveProfileSchools(anyList());
		verify(profileRepository).deleteAllProfileSkillsByProfileId(profileId);
		verify(profileRepository).saveProfileSkills(anyList());
		verify(profileRepository).deleteAllProfileRolesByProfileId(profileId);
		verify(profileRepository).saveProfileRoles(anyList());
		verify(portfolioRepository).savePortfolios(anyList());
		verify(profileRepository).saveProjectPortfolios(anyList());
	}

	@Test
	void null_필드는_업데이트하지_않는다() {
		// given
		Long userId = 1L;
		Long profileId = 1L;
		Profile profile = createProfile(profileId, userId);
		ProfileUpdateCommand command = new ProfileUpdateCommand(
			null, // schools
			null, // portfolios
			null, // skills
			null, // roleIds
			null, // selfIntroduction
			null  // residence
		);

		when(profileRepository.getProfileByUserId(userId)).thenReturn(profile);

		// when
		service.update(userId, command);

		// then
		verify(profileRepository, never()).deleteAllProfileSchoolsByProfileId(any());
		verify(profileRepository, never()).saveProfileSchools(anyList());
		verify(profileRepository, never()).deleteAllProfileSkillsByProfileId(any());
		verify(profileRepository, never()).saveProfileSkills(anyList());
		verify(profileRepository, never()).deleteAllProfileRolesByProfileId(any());
		verify(profileRepository, never()).saveProfileRoles(anyList());
		verify(profileRepository, never()).deleteAllProjectPortfoliosByProfileId(any());
		verify(portfolioRepository, never()).savePortfolios(anyList());
	}

	private Profile createProfile(Long profileId, Long userId) {
		return Profile.builder()
			.id(profileId)
			.userId(userId)
			.build();
	}

	private ProfileUpdateCommand createEmptyCommand() {
		return new ProfileUpdateCommand(
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			null,
			null
		);
	}

	private ProfileUpdateCommand createCommandWithSchools() {
		return new ProfileUpdateCommand(
			List.of(
				new ProfileUpdateCommand.SchoolUpdateRequest(
					1L,
					SchoolStateType.ENROLLED,
					"컴퓨터공학과",
					LocalDate.of(2020, 3, 1),
					LocalDate.of(2024, 2, 29)
				)
			),
			null,
			null,
			null,
			null,
			null
		);
	}

	private ProfileUpdateCommand createCommandWithSkills() {
		return new ProfileUpdateCommand(
			null,
			null,
			List.of(
				new ProfileUpdateCommand.SkillUpdateRequest(1L, null),
				new ProfileUpdateCommand.SkillUpdateRequest(2L, null)
			),
			null,
			null,
			null
		);
	}

	private ProfileUpdateCommand createCommandWithRoles() {
		return new ProfileUpdateCommand(
			null,
			null,
			null,
			List.of(1L, 3L),
			null,
			null
		);
	}

	private ProfileUpdateCommand createCommandWithEmptyPortfolios() {
		return new ProfileUpdateCommand(
			null,
			new ArrayList<>(),
			null,
			null,
			null,
			null
		);
	}

	private ProfileUpdateCommand createCommandWithExistingPortfolio(Long portfolioId) {
		return new ProfileUpdateCommand(
			null,
			List.of(
				new ProfileUpdateCommand.PortfolioUpdateRequest(
					portfolioId,
					PortfolioType.PROJECT,
					LocalDate.of(2023, 6, 1),
					LocalDate.of(2023, 8, 31),
					"수정된 내용",
					null
				)
			),
			null,
			null,
			null,
			null
		);
	}

	private ProfileUpdateCommand createCommandWithNewPortfolio() {
		return new ProfileUpdateCommand(
			null,
			List.of(
				new ProfileUpdateCommand.PortfolioUpdateRequest(
					null,
					PortfolioType.INTERN,
					LocalDate.of(2023, 6, 1),
					LocalDate.of(2023, 8, 31),
					"네이버 백엔드 인턴십",
					null
				)
			),
			null,
			null,
			null,
			null
		);
	}

	private ProfileUpdateCommand createCommandWithMixedPortfolios(Long existingPortfolioId) {
		return new ProfileUpdateCommand(
			null,
			List.of(
				new ProfileUpdateCommand.PortfolioUpdateRequest(
					existingPortfolioId,
					PortfolioType.PROJECT,
					LocalDate.of(2023, 6, 1),
					LocalDate.of(2023, 8, 31),
					"수정된 내용",
					null
				),
				new ProfileUpdateCommand.PortfolioUpdateRequest(
					null,
					PortfolioType.INTERN,
					LocalDate.of(2023, 9, 1),
					LocalDate.of(2024, 1, 31),
					"새로운 인턴십",
					null
				)
			),
			null,
			null,
			null,
			null
		);
	}

	private ProfileUpdateCommand createFullCommand() {
		return new ProfileUpdateCommand(
			List.of(
				new ProfileUpdateCommand.SchoolUpdateRequest(
					1L,
					SchoolStateType.ENROLLED,
					"컴퓨터공학과",
					LocalDate.of(2020, 3, 1),
					LocalDate.of(2024, 2, 29)
				)
			),
			List.of(
				new ProfileUpdateCommand.PortfolioUpdateRequest(
					null,
					PortfolioType.INTERN,
					LocalDate.of(2023, 6, 1),
					LocalDate.of(2023, 8, 31),
					"네이버 백엔드 인턴십",
					null
				)
			),
			List.of(
				new ProfileUpdateCommand.SkillUpdateRequest(1L, null),
				new ProfileUpdateCommand.SkillUpdateRequest(2L, null)
			),
			List.of(1L, 3L),
			null,
			null
		);
	}
}

