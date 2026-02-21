package com.sidework.profile.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.response.exception.ExceptionAdvice;
import com.sidework.profile.application.adapter.ProfileController;
import com.sidework.profile.application.adapter.UserProfileResponse;
import com.sidework.profile.application.exception.ProfileNotFoundException;
import com.sidework.profile.application.port.in.ProfileCommandUseCase;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;
import com.sidework.profile.application.port.in.ProfileUpdateCommand;
import com.sidework.profile.domain.PortfolioType;
import com.sidework.profile.domain.SchoolStateType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
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

	@Test
	void 프로필_조회_요청시_성공하면_200을_반환한다() throws Exception {
		// given
		Long userId = 1L;
		UserProfileResponse response = createUserProfileResponse(userId);
		when(profileQueryUseCase.getProfileByUserId(userId)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/profiles")
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
		mockMvc.perform(get("/api/v1/profiles")
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isSuccess").value(true))
				.andExpect(jsonPath("$.result.profileId").isEmpty());

		verify(profileQueryUseCase).getProfileByUserId(userId);
	}

	@Test
	void 프로필_수정_요청시_성공하면_200을_반환한다() throws Exception {
		// given
		ProfileUpdateCommand command = createProfileUpdateCommand();

		// when & then
		mockMvc.perform(put("/api/v1/profiles")
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
		mockMvc.perform(put("/api/v1/profiles")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(command)))
				.andDo(print())
				.andExpect(status().isNotFound());

		verify(profileCommandUseCase).update(1L, command);
	}

	private ProfileUpdateCommand createProfileUpdateCommand() {
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
			List.of(new ProfileUpdateCommand.SkillUpdateRequest(1L, null)),
			List.of(1L, 3L),
			null,
			null
		);
	}

	private UserProfileResponse createUserProfileResponse(Long userId) {
		return new UserProfileResponse(
			userId,
			"test@test.com",
			"홍길동",
			"길동이",
			25,
			"010-1234-5678",
			1L,
			null,
			null,
			0,
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
			"010-1234-5678",
			null,
			null,
			null,
			0,
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);
	}
}

