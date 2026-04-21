package com.sidework.profile.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.exception.ExceptionAdvice;
import com.sidework.profile.application.adapter.ProfileController;
import com.sidework.profile.application.adapter.UserProfileResponse;
import com.sidework.profile.application.adapter.UserProfileListResponse;
import com.sidework.profile.application.adapter.UserProjectDto;
import com.sidework.profile.application.exception.ProfileNotFoundException;
import com.sidework.profile.application.port.in.ProfileCommandUseCase;
import com.sidework.profile.application.port.in.ProfileLikeCommandUseCase;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;
import com.sidework.profile.application.port.in.ProfileUpdateCommand;
import com.sidework.common.response.PageResponse;
import com.sidework.profile.domain.PortfolioType;
import com.sidework.profile.domain.SchoolStateType;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@ContextConfiguration(classes = ProfileTestApplication.class)
@Import(ExceptionAdvice.class)
class ProfileControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ProfileQueryUseCase profileQueryUseCase;

	@MockitoBean
	private ProfileCommandUseCase profileCommandUseCase;

	@MockitoBean
	private ProfileLikeCommandUseCase profileLikeCommandUseCase;

	private AuthenticatedUserDetails authenticatedUserDetails = new AuthenticatedUserDetails(
		1L, "test@test.com", "홍길동", "password");

	@Test
	void 프로필_조회_요청시_성공하면_200을_반환한다() throws Exception {
		// given
		Long userId = 1L;
		UserProfileResponse response = createUserProfileResponse(userId);
		when(profileQueryUseCase.getProfileByUserId(userId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/profiles/me")
				.with(user(authenticatedUserDetails))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.userId").value(userId))
			.andExpect(jsonPath("$.result.email").value("test@test.com"))
			.andExpect(jsonPath("$.result.name").value("홍길동"));

		verify(profileQueryUseCase).getProfileByUserId(userId);
	}

	@Test
	void 프로필_조회_요청시_프로필이_없어도_200을_반환한다() throws Exception {
		// given
		Long userId = 1L;
		UserProfileResponse response = createUserProfileResponseWithoutProfile(userId);
		when(profileQueryUseCase.getProfileByUserId(userId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/profiles/me")
				.with(user(authenticatedUserDetails))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.profileId").value(nullValue()));

		verify(profileQueryUseCase).getProfileByUserId(userId);
	}

    @Test
    void 내_프로젝트_목록_조회_요청시_성공하면_200을_반환한다() throws Exception {
        // given
        Long userId = authenticatedUserDetails.getId();

        List<UserProjectDto> content = List.of(
                new UserProjectDto(
                        23L,
                        "테스트 프로젝트",
                        "프로젝트 설명",
                        LocalDate.of(2026, 4, 17),
                        LocalDate.of(2026, 6, 17),
                        MeetingType.HYBRID,
                        ProjectStatus.PREPARING,
                        List.of("Java"),
                        List.of(ProjectRole.OWNER, ProjectRole.BACKEND)
                )
        );

        PageResponse<List<UserProjectDto>> response = PageResponse.of(
                content,
                0,
                5,
                16,
                4
        );

        when(profileQueryUseCase.getUserProjectList(eq(userId), any(Pageable.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/profiles/me/projects")
                        .with(user(authenticatedUserDetails))
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.page").value(1))
                .andExpect(jsonPath("$.result.size").value(5))
                .andExpect(jsonPath("$.result.totalElements").value(16))
                .andExpect(jsonPath("$.result.totalPages").value(4))
                .andExpect(jsonPath("$.result.content[0].projectId").value(23L))
                .andExpect(jsonPath("$.result.content[0].title").value("테스트 프로젝트"));

        verify(profileQueryUseCase).getUserProjectList(eq(userId), any(Pageable.class));
    }

    @Test
    void 내_프로젝트_목록_조회시_page가_1보다_작으면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/profiles/me/projects")
                        .with(user(authenticatedUserDetails))
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 내_프로젝트_목록_조회시_size가_1보다_작으면_400을_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/profiles/me/projects")
                        .with(user(authenticatedUserDetails))
                        .param("page", "1")
                        .param("size", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

	@Test
	void 프로필_수정_요청시_성공하면_200을_반환한다() throws Exception {
		// given
		ProfileUpdateCommand command = createProfileUpdateCommand();

		// when & then
		mockMvc.perform(put("/api/v1/profiles/me")
				.with(user(authenticatedUserDetails))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(command)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true));

		verify(profileCommandUseCase).update(1L, command);
	}

	@Test
	void 프로필_수정_요청시_프로필이_없으면_404를_반환한다() throws Exception {
		// given
		ProfileUpdateCommand command = createProfileUpdateCommand();
		doThrow(new ProfileNotFoundException(1L))
			.when(profileCommandUseCase)
			.update(1L, command);

		// when & then
		mockMvc.perform(put("/api/v1/profiles/me")
				.with(user(authenticatedUserDetails))
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(command)))
			.andDo(print())
			.andExpect(status().isNotFound());

		verify(profileCommandUseCase).update(1L, command);
	}

	@Test
	void 프로필_좋아요_요청시_성공하면_200을_반환한다() throws Exception {
		// given
		Long profileId = 2L;

		doNothing().when(profileLikeCommandUseCase).like(1L, profileId);

		// when & then
		mockMvc.perform(post("/api/v1/profiles/{profileId}/likes", profileId)
				.with(user(authenticatedUserDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true));

		verify(profileLikeCommandUseCase).like(1L, profileId);
	}

	@Test
	void 프로필_좋아요_목록_조회시_성공하면_liked를_반환한다() throws Exception {
		// given
		Long userId = authenticatedUserDetails.getId();
		List<Long> skillIds = List.of(1L, 2L);

		UserProfileListResponse item = new UserProfileListResponse(
			10L,           // userId
			"테스트유저",
			null,          // description
			List.of(),     // skills
			true,          // liked
			4.5            // score
		);
		PageResponse<List<UserProfileListResponse>> pageResponse = PageResponse.of(
			List.of(item),
			0,
			20,
			1,
			1
		);

		when(profileQueryUseCase.getLikedUserProfileList(eq(userId), eq(skillIds), any()))
			.thenReturn(pageResponse);

		// when & then
		mockMvc.perform(get("/api/v1/profiles/me/likes")
				.with(user(authenticatedUserDetails))
				.param("skillIds", "1", "2"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.content[0].liked").value(true));

		verify(profileQueryUseCase).getLikedUserProfileList(eq(userId), eq(skillIds), any());
	}

	private ProfileUpdateCommand createProfileUpdateCommand() {
		return new ProfileUpdateCommand(
			"test@test.com", // email
			"홍길동",         // name
			"길동이",         // nickname
			25,              // age
			"010-1234-5678", // tel
			1L,              // residenceRegionId
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
			List.of(new ProfileUpdateCommand.SkillUpdateRequest(1L, null)),
			null // selfIntroduction
		);
	}

	private UserProfileResponse createUserProfileResponse(Long userId) {
		return new UserProfileResponse(
			userId,
			"test@test.com",
			"홍길동",
			"길동이",
			25,
			1L,
			null,
			null,
			0.0,
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);
	}

	private UserProfileResponse createUserProfileResponseWithoutProfile(Long userId) {
		return new UserProfileResponse(
			userId,
			"test@test.com",
			"홍길동",
			"길동이",
			25,
			null,
			null,
			null,
			0.0,
			null,
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);
	}
}

