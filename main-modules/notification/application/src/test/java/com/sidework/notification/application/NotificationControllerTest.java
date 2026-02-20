package com.sidework.notification.application;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.notification.application.port.in.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.response.exception.ExceptionAdvice;
import com.sidework.notification.application.adapter.NotificationController;
import com.sidework.notification.application.adapter.NotificationResponse;
import com.sidework.notification.application.exception.NotificationNotFoundException;
import com.sidework.common.event.sse.port.in.SseSubscribeUseCase;
import com.sidework.notification.domain.NotificationType;

@WebMvcTest(NotificationController.class)
@ContextConfiguration(classes = NotificationTestApplication.class)
@Import(ExceptionAdvice.class)
class NotificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private NotificationCommandUseCase commandUseCase;

	@MockitoBean
	private NotificationQueryUseCase queryUseCase;

	@MockitoBean
	private SseSubscribeUseCase sseSubscribeUseCase;

    @MockitoBean
    private FcmTokenCommandUseCase fcmTokenCommandUseCase;

    @MockitoBean
    private FcmPushUseCase fcmPushUseCase;

    private AuthenticatedUserDetails authenticatedUserDetails = new AuthenticatedUserDetails(
            1L,
            "test@mail.com",
            "테스터",
            "pw"
    );

	@Test
	void SSE_구독_요청_시_200을_반환한다() throws Exception {
		when(sseSubscribeUseCase.subscribeUser(1L)).thenReturn(new SseEmitter());

		mockMvc.perform(get("/api/v1/notifications/subscribe")
                        .with(user(authenticatedUserDetails))
				.accept(MediaType.TEXT_EVENT_STREAM_VALUE))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	void 알림_목록_조회_요청_시_성공하면_200과_목록을_반환한다() throws Exception {
		List<NotificationResponse> list = List.of(
			new NotificationResponse(1L, 1L, NotificationType.PROJECT_APPROVED, "제목", "내용", false)
		);
		when(queryUseCase.getByUserId(anyLong())).thenReturn(list);

		mockMvc.perform(get("/api/v1/notifications")
                        .with(user(authenticatedUserDetails))
                )
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result.length()").value(1))
			.andExpect(jsonPath("$.result[0].id").value(1))
			.andExpect(jsonPath("$.result[0].title").value("제목"))
			.andExpect(jsonPath("$.result[0].read").value(false));
	}

	@Test
	void 알림_읽음_처리_요청_시_성공하면_200을_반환한다() throws Exception {
		Long notificationId = 1L;
		doNothing().when(commandUseCase).markAsRead(eq(notificationId), anyLong());

		mockMvc.perform(patch("/api/v1/notifications/{notificationId}/read", notificationId)
                        .with(user(authenticatedUserDetails))
                        .with(csrf())
                )
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true));
	}

	@Test
	void 알림_읽음_처리_요청_시_알림이_없으면_404를_반환한다() throws Exception {
		Long notificationId = 999L;
		doThrow(new NotificationNotFoundException(notificationId))
			.when(commandUseCase).markAsRead(eq(notificationId), anyLong());

		mockMvc.perform(patch("/api/v1/notifications/{notificationId}/read", notificationId)
                        .with(user(authenticatedUserDetails))
                        .with(csrf())
                )
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	void 테스트_알림_발송_요청_시_성공하면_200을_반환한다() throws Exception {
		NotificationCommand command = new NotificationCommand("테스트 제목", "테스트 내용");
		doNothing().when(commandUseCase).create(anyLong(), eq(NotificationType.PROJECT_APPROVED), eq("테스트 제목"), eq("테스트 내용"));

		mockMvc.perform(post("/api/v1/notifications/test")
                        .with(user(authenticatedUserDetails))
                        .with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(command)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true));
	}
}
