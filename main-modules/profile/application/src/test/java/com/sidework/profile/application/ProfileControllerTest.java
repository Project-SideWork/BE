package com.sidework.profile.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.profile.application.adapter.ProfileController;
import com.sidework.profile.application.adapter.UserProfileResponse;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
class ProfileControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private ProfileQueryUseCase profileQueryUseCase;

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

	private UserProfileResponse createUserProfileResponse(Long userId) {
		return new UserProfileResponse(
			userId,
			"test@test.com",
			"홍길동",
			"길동이",
			25,
			"010-1234-5678",
			1L,
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
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>(),
			new ArrayList<>()
		);
	}
}

